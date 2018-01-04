package com.lxr.iot.bootstrap;

import com.lxr.iot.pool.Scheduled;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxr
 * @create 2018-01-04 20:33
 **/
@Slf4j
public class PublishApiSevice {

    private Scheduled scheduled;


    public PublishApiSevice(Scheduled scheduled) {
        this.scheduled =scheduled;
    }

    protected void  sendQos1(String topic, Channel channel, MqttMessage mqttMessage){
        log.info("成功发送消息:"+new String(mqttMessage.getPayload()));
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,false, MqttQoS.AT_MOST_ONCE,false,0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic,0 );
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(mqttMessage.getPayload()));
        channel.writeAndFlush(mqttPublishMessage);
    }



}
