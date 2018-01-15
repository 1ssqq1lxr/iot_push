package com.lxr.iot.bootstrap;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.properties.ConnectOptions;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;

import java.util.List;

/**
 * 生产者
 *
 * @author lxr
 * @create 2018-01-04 17:17
 **/
public interface Producer {

    Channel getChannel();

    Producer connect(ConnectOptions connectOptions);

    void  close();

    void setMqttListener(MqttListener mqttListener);

    void pub(String topic,String message,boolean retained,int qos);

    void pub(String topic,String message);

    void pub(String topic,String message,int qos);

    void pub(String topic,String message,boolean retained);

    void sub(SubMessage... subMessages);

    void unsub(List<String> topics);

    void unsub();

    void disConnect();

}
