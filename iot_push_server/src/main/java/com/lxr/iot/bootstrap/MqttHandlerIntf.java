package com.lxr.iot.bootstrap;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

/**
 * 自定义 对外暴露 消息处理api
 *
 * @author lxr
 * @create 2017-11-21 9:53
 **/
public interface MqttHandlerIntf {

    boolean login(Channel channel, MqttConnectMessage mqttConnectMessage);

    void  publish(Channel channel, MqttPublishMessage mqttPublishMessage);

    void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage);

    void close(Channel channel);

    void pong(Channel channel);


    void unsubscribe(Channel channel, MqttUnsubscribeMessage mqttMessage);

    void replyLogin(Channel channel, MqttConnectMessage mqttConnectMessage);

    void puback(Channel channel, MqttPubAckMessage mqttMessage);

    void disconnect(Channel channel, MqttMessage mqttMessage);

    void pubrec(Channel channel, MqttMessage mqttMessage);

    void pubrel(Channel channel, MqttMessage mqttMessage);

    void pubcomp(Channel channel, MqttMessage mqttMessage);


}
