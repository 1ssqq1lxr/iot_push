package com.lxr.iot.mqtt;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * mqtt协议处理器
 *
 * @author lxr
 * @create 2017-11-20 13:38
 **/
public  abstract  class MqttHander  extends SimpleChannelInboundHandler<MqttMessage> {

}
