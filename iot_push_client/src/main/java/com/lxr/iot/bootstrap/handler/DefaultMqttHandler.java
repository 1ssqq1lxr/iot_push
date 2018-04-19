package com.lxr.iot.bootstrap.handler;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.mqtt.ClientMqttHandlerService;
import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认 mqtthandler处理
 *
 * @author lxr
 * @create 2017-11-20 13:58
 **/

@ChannelHandler.Sharable
@Slf4j
public class DefaultMqttHandler extends MqttHander {


    private ClientMqttHandlerService mqttHandlerApi;

    private MqttProducer mqttProducer;

    private  MqttListener mqttListener;
    
    private ConnectOptions connectOptions;

    public DefaultMqttHandler(ConnectOptions connectOptions, ClientMqttHandlerService mqttHandlerApi, Producer producer, MqttListener mqttListener) {
        super(mqttHandlerApi);
        this.mqttProducer =(MqttProducer) producer;
        this.mqttListener = mqttListener;
        this.mqttHandlerApi=mqttHandlerApi;
        this.connectOptions=connectOptions;
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ConnectOptions.MqttOpntions mqtt = connectOptions.getMqtt();
        log.info("【DefaultMqttHandler：channelActive】"+ctx.channel().localAddress().toString()+"启动成功");
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT,false, MqttQoS.AT_LEAST_ONCE,false,10);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1_1.protocolName(),MqttVersion.MQTT_3_1_1.protocolLevel(),mqtt.isHasUserName(),mqtt.isHasPassword(),mqtt.isHasWillRetain(),mqtt.getWillQos(),mqtt.isHasWillFlag(),mqtt.isHasCleanSession(),mqtt.getKeepAliveTime());
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(mqtt.getClientIdentifier(),mqtt.getWillTopic(),mqtt.getWillMessage(),mqtt.getUserName(),mqtt.getPassword());
        MqttConnectMessage mqttSubscribeMessage = new MqttConnectMessage(mqttFixedHeader,mqttConnectVariableHeader,mqttConnectPayload);
        channel.writeAndFlush(mqttSubscribeMessage);
    }



    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()){
            case UNSUBACK:
                mqttHandlerApi.unsubBack(channelHandlerContext.channel(),mqttMessage);
                break;
            case CONNACK:
                mqttProducer.connectBack((MqttConnAckMessage) mqttMessage);
                break;
            case PUBLISH:
                publish(channelHandlerContext.channel(),(MqttPublishMessage)mqttMessage);
                break;
            case PUBACK: // qos 1回复确认
                mqttHandlerApi.puback(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREC: //
                mqttHandlerApi.pubrec(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREL: //
                mqttHandlerApi.pubrel(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBCOMP: //
                mqttHandlerApi.pubcomp(channelHandlerContext.channel(),mqttMessage);
                break;
            case SUBACK:
                mqttHandlerApi.suback(channelHandlerContext.channel(),(MqttSubAckMessage)mqttMessage);
                break;
            default:
                break;
        }
    }

    private void publish(Channel channel,MqttPublishMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttMessage.variableHeader();
        ByteBuf payload = mqttMessage.payload();
        byte[] bytes = ByteBufUtil.copyByteBuf(payload); //
        if(mqttListener!=null){
            mqttListener.callBack(mqttPublishVariableHeader.topicName(),new String(bytes));
        }
        switch (mqttFixedHeader.qosLevel()){
            case AT_MOST_ONCE:
                break;
            case AT_LEAST_ONCE:
                mqttHandlerApi.pubBackMessage(channel,mqttPublishVariableHeader.messageId());
                break;
            case EXACTLY_ONCE:
                mqttProducer.pubRecMessage(channel,mqttPublishVariableHeader.messageId());
                break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttProducer.getNettyBootstrapClient().doubleConnect();
        log.error("exception",cause);
        if(mqttListener!=null){
            mqttListener.callThrowable(cause);
        }
    }


}
