package com.lxr.iot.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;

/**
 * 抽象出服务端事件
 *
 * @author lxr
 * @create 2018-01-03 16:11
 **/
public abstract class ClientMqttHandlerService implements MqttHandlerIntf {


    public abstract void suback(Channel channel, MqttSubAckMessage mqttMessage);

    public abstract  void pingresp(Channel channel);

}

