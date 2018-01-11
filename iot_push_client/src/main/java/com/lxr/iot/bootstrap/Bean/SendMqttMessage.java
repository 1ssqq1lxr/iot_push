package com.lxr.iot.bootstrap.Bean;

import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.enums.QosStatus;
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
public class SendMqttMessage {

    private String Topic;

    private byte[] payload;

    private int qos;

    private boolean retained;

    private boolean dup;

    private int messageId;


    private long timestamp;

    private volatile ConfirmStatus confirmStatus;


}
