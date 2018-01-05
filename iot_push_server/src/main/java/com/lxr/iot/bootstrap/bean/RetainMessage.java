package com.lxr.iot.bootstrap.bean;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

/**
 * 保留消息
 * @author lxr
 * @create 2017-11-24 16:06
 **/
@Builder
@Data
public class RetainMessage {

    private byte[]  byteBuf;

    private MqttQoS qoS;
    public String getString(){
        return new String(byteBuf);
    }
}
