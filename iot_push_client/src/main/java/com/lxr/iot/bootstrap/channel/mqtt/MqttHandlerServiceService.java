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
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 客户端channelService
 *
 * @author lxr
 * @create 2018-01-02 20:48
 **/
@Slf4j
public class MqttHandlerServiceService extends  ClientMqttHandlerService{

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
    public void pubrec(Channel channel, MqttPubAckMessage mqttMessage ) {
        int messageId = mqttMessage.variableHeader().messageId();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,false, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        Optional.ofNullable(Cache.get(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.PUBREL);
        });
        channel.writeAndFlush(mqttPubAckMessage);
    }

    @Override
    public void pubrel(Channel channel, MqttPubAckMessage mqttMessage ) {
        int messageId = mqttMessage.variableHeader().messageId();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        Optional.ofNullable(Cache.get(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
        });
        channel.writeAndFlush(mqttPubAckMessage);
    }

    @Override
    public void pubcomp(Channel channel,MqttPubAckMessage mqttMessage ) {
        int messageId = mqttMessage.variableHeader().messageId();
        Optional.ofNullable(Cache.get(messageId)).ifPresent(sendMqttMessage -> {
            sendMqttMessage.setConfirmStatus(ConfirmStatus.COMPLETE);
        });
    }

    @Override
    public void heart(Channel channel, IdleStateEvent evt) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessage  = new MqttMessage(fixedHeader);
        channel.writeAndFlush(mqttMessage);
    }
    

}
