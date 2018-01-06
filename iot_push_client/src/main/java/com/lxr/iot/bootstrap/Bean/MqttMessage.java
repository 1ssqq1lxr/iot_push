package com.lxr.iot.bootstrap.Bean;

import lombok.Builder;
import lombok.Data;

/**
 * 消息
 *
 * @author lxr
 * @create 2018-01-04 19:36
 **/
@Data
@Builder
public class MqttMessage {

    private String Topic;

    private byte[] payload;

    private int qos;

    private boolean retained;

    private boolean dup;

    private int messageId;

    private boolean time;
}
