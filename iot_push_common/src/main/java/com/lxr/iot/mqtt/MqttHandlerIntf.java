package com.lxr.iot.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义 对外暴露 消息处理api
 *
 * @author lxr
 * @create 2017-11-21 9:53
 **/
public interface MqttHandlerIntf {

    void close(Channel channel);

    void puback(Channel channel, MqttMessage mqttMessage);

    void pubrec(Channel channel, MqttMessage mqttMessage);

    void pubrel(Channel channel, MqttMessage mqttMessage);

    void pubcomp(Channel channel, MqttMessage mqttMessage);

    void doTimeOut(Channel channel, IdleStateEvent evt);


}
