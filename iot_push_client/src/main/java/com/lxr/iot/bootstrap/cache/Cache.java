package com.lxr.iot.bootstrap.cache;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存
 *
 * @author lxr
 * @create 2018-01-04 20:15
 **/
public class Cache {

    private static  ConcurrentHashMap<Integer,SendMqttMessage> message = new ConcurrentHashMap<>();


    public static  boolean put(Integer messageId,SendMqttMessage mqttMessage){

        return message.put(messageId,mqttMessage)==null;

    }

    public static SendMqttMessage get(Integer messageId){

        return  message.get(messageId);

    }


    public static SendMqttMessage del(Integer messageId){
        return message.remove(messageId);
    }
}
