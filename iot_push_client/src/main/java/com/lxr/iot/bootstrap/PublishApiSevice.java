package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.*;
import com.lxr.iot.pool.Scheduled;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

/**
 * @author lxr
 * @create 2018-01-04 20:33
 **/
@Slf4j
public class PublishApiSevice {

//    private Scheduled scheduled;
//
//
//    public PublishApiSevice(Scheduled scheduled) {
//        this.scheduled =scheduled;
//    }


    protected  void sendQosMessage(Channel channel, com.lxr.iot.bootstrap.Bean.MqttMessage mqttMessage){
        log.info("成功发送消息:"+new String(mqttMessage.getPayload()));
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,mqttMessage.isDup(), MqttQoS.valueOf(mqttMessage.getQos()),mqttMessage.isRetained(),0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(mqttMessage.getTopic(),mqttMessage.getMessageId());
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(mqttMessage.getPayload()));
        channel.writeAndFlush(mqttPublishMessage);
    }

//
//    protected void  sendQos0(Channel channel, com.lxr.iot.bootstrap.Bean.MqttMessage mqttMessage){
//        log.info("成功发送消息:"+new String(mqttMessage.getPayload()));
//        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,mqttMessage.isDup(), MqttQoS.AT_MOST_ONCE,mqttMessage.isRetained(),0);
//        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(mqttMessage.getTopic(),0 );
//        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(mqttMessage.getPayload()));
//        channel.writeAndFlush(mqttPublishMessage);
//    }
//
//    /**
//     * 发送 qos1 类的消息
//     * @param channel
//     * @param mqttMessage
//     */
//    protected   void  sendQos1(Channel channel, com.lxr.iot.bootstrap.Bean.MqttMessage mqttMessage){
//        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,mqttMessage.isDup(), MqttQoS.AT_LEAST_ONCE,mqttMessage.isRetained(),0);
//        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(mqttMessage.getTopic(),mqttMessage.getMessageId());
//        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(mqttMessage.getPayload()));
//        channel.writeAndFlush(mqttPublishMessage);
//        if(mqttMessage.isTime()){
//            AttributeKey<ScheduledFuture> attributeKey = AttributeKey.valueOf("qos1"+mqttMessage.getMessageId());
//            channel.attr(attributeKey).set(scheduled.submit(() -> {
//                mqttMessage.setTime(false);
//                mqttMessage.setDup(true);
//                log.info("PublishApiSevice sendQos1Ms1g :"+channel.remoteAddress()+"【meaasgeId:"+mqttMessage.getMessageId()+"】重复发送消息【topic："+mqttMessage.getTopic()+"】");
//                sendQos1(channel,mqttMessage);
//            }));
//        }
//    }



}
