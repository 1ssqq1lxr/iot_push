package com.lxr.iot.bootstrap.channel.mqtt;

import com.lxr.iot.bootstrap.ChannelService;
import com.lxr.iot.mqtt.publish.PublishApiSevice;
import com.lxr.iot.pool.Scheduled;

/**
 * 处理类
 *
 * @author lxr
 * @create 2018-01-04 10:19
 **/
public class MqttChannelService extends PublishApiSevice implements ChannelService {

    public MqttChannelService(Scheduled scheduled) {
        super(scheduled);
    }
}
