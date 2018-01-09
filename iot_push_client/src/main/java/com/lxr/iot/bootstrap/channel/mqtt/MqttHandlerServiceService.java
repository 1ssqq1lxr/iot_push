package com.lxr.iot.bootstrap.channel.mqtt;

import com.lxr.iot.bootstrap.Bean.*;
import com.lxr.iot.bootstrap.cache.Cache;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.mqtt.ClientMqttHandlerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端channelService
 *
 * @author lxr
 * @create 2018-01-02 20:48
 **/
@Slf4j
public class MqttHandlerServiceService extends ClientMqttHandlerService {

    @Override
    public void close(Channel channel) {
        channel.close();
    }

    @Override
    public void puback(Channel channel, MqttPubAckMessage mqttMessage) {
        int messageId = mqttMessage.variableHeader().messageId();
        SendMqttMessage sendMqttMessage = Cache.del(messageId);
        sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
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

    @Override
    public void suback(Channel channel, MqttSubAckMessage mqttMessage) {

    }

    @Override
    public void pingresp(Channel channel) {

    }
}
