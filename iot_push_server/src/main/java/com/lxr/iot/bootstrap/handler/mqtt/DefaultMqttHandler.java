package com.lxr.iot.bootstrap.handler.mqtt;

import com.lxr.iot.bootstrap.ChannelService;
import com.lxr.iot.exception.NoFindHandlerException;
import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import com.lxr.iot.mqtt.ServerMqttHandlerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ChannelService channelService;


    public DefaultMqttHandler(MqttHandlerIntf mqttHandlerApi, MqttHandlerIntf mqttHandlerApi1) {
        super(mqttHandlerApi);
        this.mqttHandlerApi = mqttHandlerApi1;
    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        ServerMqttHandlerService serverMqttHandlerService;
        if(mqttHandlerApi instanceof ServerMqttHandlerService){
            serverMqttHandlerService =(ServerMqttHandlerService)mqttHandlerApi;
        }
        else{
            throw new NoFindHandlerException("handler 不匹配");
        }
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        if(mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)){
            loginCheck(serverMqttHandlerService,channelHandlerContext.channel(), (MqttConnectMessage) mqttMessage) ;
            return ;
        }
        if(channelService.getMqttChannel(channelService.getDeviceId(channelHandlerContext.channel())).isLogin()){
            switch (mqttFixedHeader.messageType()){
                case PUBLISH:
                    serverMqttHandlerService.publish(channelHandlerContext.channel(), (MqttPublishMessage) mqttMessage);
                    break;
                case SUBSCRIBE:
                    serverMqttHandlerService.subscribe(channelHandlerContext.channel(), (MqttSubscribeMessage) mqttMessage);
                    break;
                case PINGREQ:
                    serverMqttHandlerService.pong(channelHandlerContext.channel());
                    break;
                case DISCONNECT:
                    serverMqttHandlerService.disconnect(channelHandlerContext.channel(),mqttMessage);
                    break;
                case UNSUBSCRIBE:
                    serverMqttHandlerService.unsubscribe(channelHandlerContext.channel(),(MqttUnsubscribeMessage)mqttMessage);
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
    public void loginCheck(ServerMqttHandlerService serverMqttHandlerService, Channel channel, MqttConnectMessage mqttConnectMessage){
        if(serverMqttHandlerService.login(channel, mqttConnectMessage)){
            serverMqttHandlerService.replyLogin(channel,mqttConnectMessage);
        }
        else{
            channel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttHandlerApi.close(ctx.channel());
    }
}
