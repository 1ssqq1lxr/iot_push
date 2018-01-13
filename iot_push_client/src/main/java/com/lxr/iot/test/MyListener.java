package com.lxr.iot.test;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.auto.MqttMessageListener;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.stereotype.Service;

/**
 * @author lxr
 * @create 2018-01-12 15:14
 **/
@Service
@MqttMessageListener(qos = MqttQoS.AT_LEAST_ONCE,topic = "/topic")
public class MyListener implements MqttListener{
    @Override
    public void callBack(String topic, String msg) {
    }

    @Override
    public void callThrowable(Throwable e) {

    }
}
