package com.lxr.iot.client;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.auto.MqttMessageListener;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author lxr
 * @create 2018-01-12 15:14
 **/
@Slf4j
@Service
@MqttMessageListener(qos = MqttQoS.AT_LEAST_ONCE,topic = "/t1/t2")
public class MyListener implements MqttListener{
    @Override
    public void callBack(String topic, String msg) {
        log.info("============================="+topic+msg);
    }

    @Override
    public void callThrowable(Throwable e) {
            log.info("exception",e);
    }
}
