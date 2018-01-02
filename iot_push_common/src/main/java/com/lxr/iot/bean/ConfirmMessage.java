package com.lxr.iot.bean;

import com.lxr.iot.enums.QosStatus;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

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
