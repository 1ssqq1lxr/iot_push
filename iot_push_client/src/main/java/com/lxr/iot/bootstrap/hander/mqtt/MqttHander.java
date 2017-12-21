package com.lxr.iot.bootstrap.hander.mqtt;/**
 * Created by wangcy on 2017/12/21.
 */

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * handler处理
 *
 * @author lxr
 * @create 2017-12-21 16:29
 **/
public abstract class MqttHander  extends   SimpleChannelInboundHandler<MqttMessage>{
}
