package com.lxr.iot.example;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.properties.ConnectOptions;

/**
 * 测试
 *
 * @author lxr
 * @create 2018-01-10 10:09
 **/
public class MqttMain {

    public static void main(String[] strings){
        Producer producer = new MqttProducer();
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setBacklog(1024);
        connectOptions.setConnectTime(1000l);
        connectOptions.setSsl(false);
        connectOptions.setJksStorePassword("mu$tch8ng3");
        connectOptions.setJksFile("/securesocket.jks");
        connectOptions.setJksCertificatePassword("inc0rrect");
        connectOptions.setServerIp("192.168.0.176");
        connectOptions.setPort(1884);
        connectOptions.setBossThread(1);
        connectOptions.setWorkThread(8);
        connectOptions.setMinPeriod(10);
        connectOptions.setRevbuf(1024);
        connectOptions.setSndbuf(1024);
        connectOptions.setTcpNodelay(true);
        connectOptions.setKeepalive(true);
        ConnectOptions.MqttOpntions mqttOpntions = new ConnectOptions.MqttOpntions();
        mqttOpntions.setHasCleanSession(true);
        mqttOpntions.setHasWillFlag(true);
        mqttOpntions.setClientIdentifier("111");
        mqttOpntions.setWillTopic("/lose/device");
        mqttOpntions.setWillQos(1);
        mqttOpntions.setHasWillRetain(false);
        mqttOpntions.setWillMessage("123123");
        mqttOpntions.setHasPassword(false);
        mqttOpntions.setHasPassword(false);
        connectOptions.setMqtt(mqttOpntions);
        producer.setMqttListener(new MqttListener() {
            @Override
            public void callBack(String topic, String msg) {
                        System.out.print("========================================"+topic+msg);
            }
            @Override
            public void callThrowable(Throwable e) {

            }
        });
        producer.connect(connectOptions);
//        producer.sub(SubMessage.builder().qos(MqttQoS.AT_LEAST_ONCE).topic("/t1/t2").build());
        producer.pub("/t1/t2","hah",2);
    }

}
