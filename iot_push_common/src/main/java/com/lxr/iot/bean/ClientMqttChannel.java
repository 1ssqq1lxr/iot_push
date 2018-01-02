package com.lxr.iot.bean;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端mqttchannel
 *
 * @author lxr
 * @create 2018-01-02 20:29
 **/
@Getter
@Setter
public class ClientMqttChannel {

    private Channel channel;

    private List<String> topic;

    private ConcurrentHashMap<Integer,ConfirmMessage> message ; // messageId - message(qos1)  // 待确认消息

}
