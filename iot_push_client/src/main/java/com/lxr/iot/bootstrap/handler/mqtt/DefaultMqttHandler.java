package com.lxr.iot.bootstrap.handler.mqtt;

import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.bootstrap.channel.mqtt.MqttListener;
import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import com.lxr.iot.mqtt.ByteBufUtil;
import io.netty.buffer.ByteBuf;
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


    private  MqttHandlerIntf mqttHandlerApi;

    private Producer producer;

    private final MqttListener mqttListener;


    public DefaultMqttHandler(MqttHandlerIntf mqttHandlerApi, Producer producer, MqttListener mqttListener) {
        super(mqttHandlerApi);
        this.producer = producer;
        this.mqttListener = mqttListener;
        this.mqttHandlerApi=mqttHandlerApi;
    }



    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()){
            case CONNACK:
                producer.connectBack((MqttConnAckMessage) mqttMessage);
                break;
            case PUBLISH:
                publish((MqttPublishMessage)mqttMessage);
            case PUBACK: // qos 1回复确认
                mqttHandlerApi.puback(channelHandlerContext.channel(),(MqttPubAckMessage)mqttMessage);
                break;
            case PUBREC: // 待实现
                mqttHandlerApi.pubrec(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBREL: // 待实现
                mqttHandlerApi.pubrel(channelHandlerContext.channel(),mqttMessage);
                break;
            case PUBCOMP: // 待实现
                mqttHandlerApi.pubcomp(channelHandlerContext.channel(),mqttMessage);
                break;
            default:
                break;
        }
    }

    private void publish(MqttPublishMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttMessage.variableHeader();
        ByteBuf payload = mqttMessage.payload();
        byte[] bytes = ByteBufUtil.copyByteBuf(payload); //
        if(mqttListener!=null){
            mqttListener.callBack(mqttPublishVariableHeader.topicName(),new String(bytes));
        }
        int messageId = mqttPublishVariableHeader.messageId();
        switch (mqttFixedHeader.qosLevel()){
            case AT_LEAST_ONCE:
            case AT_MOST_ONCE:
            case EXACTLY_ONCE:
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttHandlerApi.close(ctx.channel());
        if(mqttListener!=null){
            mqttListener.callThrowable(cause);
        }
    }


}
