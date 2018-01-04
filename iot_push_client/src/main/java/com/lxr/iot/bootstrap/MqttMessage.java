package com.lxr.iot.bootstrap;

import lombok.Data;

/**
 * 消息
 *
 * @author lxr
 * @create 2018-01-04 19:36
 **/
@Data
public class MqttMessage {

    private boolean mutable;

    private byte[] payload;

    private int qos;

    private boolean retained;

    private boolean dup;

    private int messageId;
}
