package com.lxr.iot.bootstrap;

import io.netty.channel.Channel;

/**
 * mqtt api操作类
 *
 * @author lxr
 * @create 2018-01-04 15:10
 **/
public class MqttProducer {

    private final Channel channel;

    public MqttProducer(Channel channel) {
        this.channel = channel;
    }


}
