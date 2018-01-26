package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.util.MessageId;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * 操作api 处理主动发送请求
 *
 * @author lxr
 * @create 2018-01-10 9:36
 **/
@Slf4j
public class MqttApi {




    protected ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


    protected  void pubMessage(Channel channel, SendMqttMessage mqttMessage){
        log.info("成功发送消息:"+new String(mqttMessage.getPayload()));
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,mqttMessage.isDup(), MqttQoS.valueOf(mqttMessage.getQos()),mqttMessage.isRetained(),0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(mqttMessage.getTopic(),mqttMessage.getMessageId());
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(mqttMessage.getPayload()));
        channel.writeAndFlush(mqttPublishMessage);
    }

    protected void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId){
        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptions);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE,false, MqttQoS.AT_LEAST_ONCE,false,0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader =MqttMessageIdVariableHeader.from(messageId);
        MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader,mqttMessageIdVariableHeader,mqttSubscribePayload);
        channel.writeAndFlush(mqttSubscribeMessage);

    }

    protected void  sendAck(MqttMessageType type,boolean isDup,Channel channel, int messageId){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(type,isDup, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
    }

    protected void pubRecMessage(Channel channel,int messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttMessage mqttPubAckMessage = new MqttMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
    }

    protected  void unSubMessage(Channel channel,List<String> topic,int messageId){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttUnsubscribePayload MqttUnsubscribeMessage = new MqttUnsubscribePayload(topic);
        MqttUnsubscribeMessage mqttUnsubscribeMessage = new MqttUnsubscribeMessage(mqttFixedHeader,variableHeader,MqttUnsubscribeMessage);
        channel.writeAndFlush(mqttUnsubscribeMessage);
    }

    protected void sendDisConnect(Channel channel){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.DISCONNECT,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader);
        channel.writeAndFlush(mqttMessage);
    }

    protected AttributeKey<ScheduledFuture<?>> getKey(String id){
        return   AttributeKey.valueOf(id);
    }




}
