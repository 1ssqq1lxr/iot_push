package com.lxr.iot.bootstrap.channel.mqtt;

/**
 * call back
 * @author lxr
 * @create 2018-01-04 18:42
 **/
public interface MqttListener{

    void callBack(String topic,String msg);

    void callThrowable(Throwable e);
}
