package com.lxr.iot.bootstrap.queue;

import com.lxr.iot.bootstrap.bean.SendMqttMessage;
import lombok.Data;

@Data
public class MessageEvent {

    private SendMqttMessage message;


}
