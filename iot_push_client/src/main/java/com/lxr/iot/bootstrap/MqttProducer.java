package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.properties.ConnectOptions;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;


/**
 * mqtt api操作类
 *
 * @author lxr
 * @create 2018-01-04 15:10
 **/
@Slf4j
public class MqttProducer  extends  AbsMqttProducer{


    public  MqttProducer connect(ConnectOptions connectOptions){
        connectTo(connectOptions);
        return this;
    }

    public void pub(String topic,MqttMessage mqttMessage){
        switch (MqttQoS.valueOf(mqttMessage.getQos())){
            case AT_MOST_ONCE:
                sendQos1(topic,channel,mqttMessage);
            case AT_LEAST_ONCE:
            case EXACTLY_ONCE:
        }
    }


    public void sub(SubMessage... subMessages){

    }

}
