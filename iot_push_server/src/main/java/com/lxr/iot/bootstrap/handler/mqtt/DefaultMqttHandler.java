package com.lxr.iot.bootstrap.handler.mqtt;

import com.lxr.iot.bootstrap.MqttHandlerIntf;
import com.lxr.iot.bootstrap.channel.mqtt.PingPongService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
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
@Data
@Slf4j
@Component
public class DefaultMqttHandler extends MqttHander {



    @Autowired
    private MqttHandlerIntf mqttHandlerApi;

    @Autowired
    PingPongService pingPongService;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
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


    private void loginCheck(Channel channel,MqttConnectMessage mqttConnectMessage){
        if(mqttHandlerApi.login(channel, mqttConnectMessage)){
            mqttHandlerApi.replyLogin(channel,mqttConnectMessage);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("【DefaultMqttHandler：channelActive】"+ctx.channel().remoteAddress().toString()+"启动成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("【DefaultMqttHandler：channelInactive】"+ctx.channel().remoteAddress().toString()+"关闭成功");
        mqttHandlerApi.close(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            pingPongService.doTimeOut(ctx.channel(),(IdleStateEvent)evt);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        mqttHandlerApi.close(ctx.channel());
        log.error("【DefaultMqttHandler：exceptionCaught】",cause);
    }

}
