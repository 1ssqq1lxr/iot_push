package com.lxr.iot.bootstrap.channel.mqtt;

import com.lxr.iot.mqtt.ClientMqttHandler;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 客户端channelService
 *
 * @author lxr
 * @create 2018-01-02 20:48
 **/
@Slf4j
public class MqttHandlerService extends ClientMqttHandler {

    @Override
    public void close(Channel channel) {

    }

    @Override
    public void puback(Channel channel, MqttPubAckMessage mqttMessage) {

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
