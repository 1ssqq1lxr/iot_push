package com.lxr.iot.bootstrap.cache;

import com.lxr.iot.bootstrap.Bean.MqttMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存
 *
 * @author lxr
 * @create 2018-01-04 20:15
 **/
public class Cache {

    private static final ConcurrentHashMap<String,MqttMessage> message = new ConcurrentHashMap<>();


    public boolean put(String topic,MqttMessage mqttMessage){

        return message.put(topic,mqttMessage)==null;

    }
}
