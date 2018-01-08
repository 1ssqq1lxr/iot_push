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

    private static  ConcurrentHashMap<String,MqttMessage> message = new ConcurrentHashMap<>();


    public boolean put(String messageId,MqttMessage mqttMessage){

        return message.put(messageId,mqttMessage)==null;

    }

    public MqttMessage get(String messageId){

        return  message.get(messageId);

    }


    public void del(String messageId){
        message.remove(messageId);
    }
}
