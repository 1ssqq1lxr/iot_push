package com.lxr.iot.bootstrap.bean;

import com.lxr.iot.enums.ConfirmStatus;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Builder;
import lombok.Data;

/**
 * mqtt 消息
 *
 * @author lxr
 * @create 2018-01-17 19:54
 **/
@Builder
@Data
public class SendMqttMessage {

    private int messageId;

    private MqttMessage mqttMessage;

    private Channel channel;

    private ConfirmStatus confirmStatus;

    private long time;



}
