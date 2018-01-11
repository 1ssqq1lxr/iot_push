package com.lxr.iot.test;

import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.properties.ConnectOptions;

/**
 * 测试
 *
 * @author lxr
 * @create 2018-01-10 10:09
 **/
public class main {

    public static void main(String[] strings){
        Producer producer = new MqttProducer();
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setBacklog(1024);
        connectOptions.setConnectTime(1000l);
        connectOptions.setSsl(false);
        connectOptions.setServerIp("127.0.0.1");
        connectOptions.setPort(1884);
        connectOptions.setBossThread(1);
        connectOptions.setWorkThread(8);
        connectOptions.setMinPeriod(10);
        connectOptions.setRevbuf(1024);
        connectOptions.setSndbuf(1024);
        connectOptions.setTcpNodelay(true);
        connectOptions.setKeepalive(true);
        ConnectOptions.MqttOpntions mqttOpntions = new ConnectOptions.MqttOpntions();
        mqttOpntions.setCleanSession(true);
        mqttOpntions.setWillFlag(false);
        mqttOpntions.setClientIdentifier("111");
        mqttOpntions.setKeepAliveTimeSeconds(20);
        mqttOpntions.setHasPassword(false);
        mqttOpntions.setHasPassword(false);
        connectOptions.setMqtt(mqttOpntions);
        producer.connect(connectOptions);
        producer.pub("/topic","hah",1);
    }

}
