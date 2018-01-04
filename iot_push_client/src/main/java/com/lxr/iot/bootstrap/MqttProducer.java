package com.lxr.iot.bootstrap;

import com.lxr.iot.properties.ConnectOptions;
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

    public void pub(MqttMessage mqttMessage){
    }

    public void sub(SubMessage... subMessages){

    }

}
