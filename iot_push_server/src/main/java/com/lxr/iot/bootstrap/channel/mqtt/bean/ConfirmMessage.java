package com.lxr.iot.bootstrap.channel.mqtt.bean;

import com.lxr.iot.bootstrap.channel.mqtt.enums.QosStatus;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 确认消息
 *
 * @author lxr
 * @create 2017-11-28 11:59
 **/
@Builder
@Data
public class ConfirmMessage {

    private byte[]  byteBuf;

    private MqttQoS qos;

    private String topic;

    private QosStatus qosStatus;

    
    public String getString(){
        return new String(byteBuf);
    }



}
