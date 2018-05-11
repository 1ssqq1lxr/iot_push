package com.lxr.iot.bootstrap.handler;

import com.lxr.iot.bootstrap.ChannelService;
import com.lxr.iot.bootstrap.bean.MqttChannel;
import com.lxr.iot.exception.NoFindHandlerException;
import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import com.lxr.iot.mqtt.ServerMqttHandlerService;
import com.lxr.iot.properties.ConnectOptions;
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


    public DefaultMqttHandler(MqttHandlerIntf mqttHandlerApi) {
        super(mqttHandlerApi);
        this.mqttHandlerApi = mqttHandlerApi;
    }

    @Override
    public void doMessage(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        Channel channel = channelHandlerContext.channel();
        ServerMqttHandlerService serverMqttHandlerService;
        if(mqttHandlerApi instanceof ServerMqttHandlerService){
            serverMqttHandlerService =(ServerMqttHandlerService)mqttHandlerApi;
        }
        else{
            throw new NoFindHandlerException("server handler 不匹配");
        }
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        if(mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)){
            if(!serverMqttHandlerService.login(channel, (MqttConnectMessage) mqttMessage)){
                channel.close();
            }
            return ;
        }
        MqttChannel mqttChannel = channelService.getMqttChannel(channelService.getDeviceId(channel));
        if(mqttChannel!=null && mqttChannel.isLogin()){
            switch (mqttFixedHeader.messageType()){
                case PUBLISH:
                    serverMqttHandlerService.publish(channel, (MqttPublishMessage) mqttMessage);
                    break;
                case SUBSCRIBE:
                    serverMqttHandlerService.subscribe(channel, (MqttSubscribeMessage) mqttMessage);
                    break;
                case PINGREQ:
                    serverMqttHandlerService.pong(channel);
                    break;
                case DISCONNECT:
                    serverMqttHandlerService.disconnect(channel);
                    break;
                case UNSUBSCRIBE:
                    serverMqttHandlerService.unsubscribe(channel,(MqttUnsubscribeMessage)mqttMessage);
                    break;
                case PUBACK:
                    mqttHandlerApi.puback(channel,mqttMessage);
                    break;
                case PUBREC:
                    mqttHandlerApi.pubrec(channel,mqttMessage);
                    break;
                case PUBREL:
                    mqttHandlerApi.pubrel(channel,mqttMessage);
                    break;
                case PUBCOMP:
                    mqttHandlerApi.pubcomp(channel,mqttMessage);
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("【DefaultMqttHandler：channelActive】"+ctx.channel().remoteAddress().toString()+"链接成功");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception",cause);
        mqttHandlerApi.close(ctx.channel());
    }
}
