package com.lxr.iot.bootstrap.handler.mqtt;

import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.mqtt.MqttHandlerIntf;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认 mqtthandler处理
 *
 * @author lxr
 * @create 2017-11-20 13:58
 **/

@ChannelHandler.Sharable
@Slf4j
@Component
public class DefaultMqttHandler extends MqttHander {


    private final MqttHandlerIntf mqttHandlerApi;


    public DefaultMqttHandler(MqttHandlerIntf mqttHandlerApi, MqttHandlerIntf mqttHandlerApi1) {
        super(mqttHandlerApi);
        this.mqttHandlerApi = mqttHandlerApi1;
    }

}
