package com.lxr.iot.auto;

/**
 * call scan
 * @author lxr
 * @create 2018-01-04 18:42
 **/
public interface MqttListener{

    void callBack(String topic,String msg);

    void callThrowable(Throwable e);
}
