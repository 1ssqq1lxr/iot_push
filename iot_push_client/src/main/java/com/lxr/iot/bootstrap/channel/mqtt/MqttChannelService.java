package com.lxr.iot.bootstrap.channel.mqtt;

import com.lxr.iot.mqtt.MqttHandlerIntf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 客户端channelService
 *
 * @author lxr
 * @create 2018-01-02 20:48
 **/
public class MqttChannelService implements MqttHandlerIntf {

    @Override
    public boolean login(Channel channel, MqttConnectMessage mqttConnectMessage) {
        return false;
    }

    @Override
    public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {

    }

    @Override
    public void subscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage) {

    }

    @Override
    public void close(Channel channel) {

    }

    @Override
    public void pong(Channel channel) {

    }

    @Override
    public void unsubscribe(Channel channel, MqttUnsubscribeMessage mqttMessage) {

    }

    @Override
    public void replyLogin(Channel channel, MqttConnectMessage mqttConnectMessage) {

    }

    @Override
    public void puback(Channel channel, MqttPubAckMessage mqttMessage) {

    }

    @Override
    public void disconnect(Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubrec(Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubrel(Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void pubcomp(Channel channel, MqttMessage mqttMessage) {

    }

    @Override
    public void doTimeOut(Channel channel, IdleStateEvent evt) {

    }
}
