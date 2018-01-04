package com.lxr.iot.bootstrap.channel.mqtt;

/**
 * call back
 * @author lxr
 * @create 2018-01-04 18:42
 **/
public interface MqttListener{

    abstract  void callBack(String topic,String msg);

    abstract  void callExeption(Exception e);
}
