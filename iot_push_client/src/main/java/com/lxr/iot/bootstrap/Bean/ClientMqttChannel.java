package com.lxr.iot.bootstrap.Bean;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

}
