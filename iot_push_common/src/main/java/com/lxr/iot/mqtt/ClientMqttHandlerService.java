package com.lxr.iot.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 抽象出服务端事件
 *
 * @author lxr
 * @create 2018-01-03 16:11
 **/
public abstract class ClientMqttHandlerService implements MqttHandlerIntf {

    @Override
    public void doTimeOut(Channel channel, IdleStateEvent evt) {
        heart(channel,evt);
    }

    public abstract void  heart(Channel channel, IdleStateEvent evt);

}

