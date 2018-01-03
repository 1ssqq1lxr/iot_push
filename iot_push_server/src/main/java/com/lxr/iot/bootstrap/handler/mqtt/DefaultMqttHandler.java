package com.lxr.iot.bootstrap.handler.mqtt;

import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认 mqtthandler处理
 *
 * @author lxr
 * @create 2017-11-20 13:58
 **/

@ChannelHandler.Sharable
@Slf4j
@Component
public class DefaultMqttHandler extends MqttHander {


    private final MqttHandlerIntf mqttHandlerApi;


    public DefaultMqttHandler(MqttHandlerIntf mqttHandlerApi, MqttHandlerIntf mqttHandlerApi1) {
        super(mqttHandlerApi);
        this.mqttHandlerApi = mqttHandlerApi1;
    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttHandlerIntf mqttHandlerApi, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()){
            case CONNECT:
                loginCheck(channelHandlerContext.channel(), (MqttConnectMessage) mqttMessage) ;
                break;
            case PUBLISH:
                mqttHandlerApi.publish(channelHandlerContext.channel(), (MqttPublishMessage) mqttMessage);
                break;
            case SUBSCRIBE:
                mqttHandlerApi.subscribe(channelHandlerContext.channel(), (MqttSubscribeMessage) mqttMessage);
                break;
            case PINGREQ:
                mqttHandlerApi.pong(channelHandlerContext.channel());
                break;
            case DISCONNECT:
                mqttHandlerApi.disconnect(channelHandlerContext.channel(),mqttMessage);
                break;
            case UNSUBSCRIBE:
                mqttHandlerApi.unsubscribe(channelHandlerContext.channel(),(MqttUnsubscribeMessage)mqttMessage);
                break;
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
}
