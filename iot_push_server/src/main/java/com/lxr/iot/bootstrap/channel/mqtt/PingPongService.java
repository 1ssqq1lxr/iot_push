package com.lxr.iot.bootstrap.channel.mqtt;

import com.lxr.iot.mqtt.MqttHandlerIntf;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 处理心跳的超时service
 *
 * @author lxr
 * @create 2017-11-22 19:22
 **/
@Slf4j
@Service
public class PingPongService {

    @Autowired
    MqttHandlerIntf mqttHandlerIntf;

}
