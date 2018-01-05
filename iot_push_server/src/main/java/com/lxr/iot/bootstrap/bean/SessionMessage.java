package com.lxr.iot.bootstrap.bean;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

/**
 * Session会话数据保存
 *
 * @author lxr
 * @create 2017-11-27 19:28
 **/
@Builder
@Data

public class SessionMessage {

    private byte[]  byteBuf;

    private MqttQoS qoS;

    private  String topic;


    public String getString(){
        return new String(byteBuf);
    }
}
