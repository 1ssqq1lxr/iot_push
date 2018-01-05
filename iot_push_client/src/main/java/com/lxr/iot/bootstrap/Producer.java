package com.lxr.iot.bootstrap;

import com.lxr.iot.properties.ConnectOptions;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;

/**
 * 生产者
 *
 * @author lxr
 * @create 2018-01-04 17:17
 **/
public interface Producer {

    void connectTo(ConnectOptions connectOptions);

    void connectBack(MqttConnAckMessage mqttConnAckMessage);

    void  close();

    void pub(String topic,MqttMessage mqttMessage);

    void sub(SubMessage... subMessages);

}
