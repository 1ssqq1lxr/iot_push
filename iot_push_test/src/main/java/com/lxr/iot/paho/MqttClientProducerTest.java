package com.lxr.iot.paho;

import com.lxr.iot.ssl.SecureSocketSslContextFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;

/**
 * 测试客户端
 *
 * @author lxr
 * @create 2017-11-28 14:14
 **/
public class MqttClientProducerTest {

    private static int qos = 1; //只有一次
    private static String broker = "ssl://127.0.0.1:1884";
    private static String userName = "tuyou";
    private static String passWord = "tuyou";


    private static MqttClient connect(String clientId, String userName,
                                          String password) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(10);
        connOpts.setKeepAliveInterval(20);
        SSLSocketFactory socketFactory = SecureSocketSslContextFactory.getClientContext().getSocketFactory();
        connOpts.setSocketFactory(socketFactory);
        connOpts.setWill("/test","haha".getBytes(),0,false);
//      String[] uris = {"tcp://10.100.124.206:1883","tcp://10.100.124.207:1883"};
//      connOpts.setServerURIs(uris);  //起到负载均衡和高可用的作用
        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
        mqttClient.setCallback(new PushCallback("test"));
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    private static void pub(MqttClient sampleClient, String msg, String topic)
            throws MqttPersistenceException, MqttException {
        MqttMessage message = null;
        try {
            message = new MqttMessage("测试一下".getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setQos(qos);
        message.setRetained(true);
        sampleClient.publish(topic, message);
    }

    private static void publish(String str,String clientId,String topic) throws MqttException{
        MqttClient mqttClient = connect(clientId,userName,passWord);
        if (mqttClient != null) {
            pub(mqttClient, str, topic);
            System.out.println("pub-->" + str);
        }

//        if (mqttClient != null) {
//            mqttClient.connect();
//        }
    }

    public static void main(String[] args) throws MqttException {
        publish("message content","client-id-2","/t1/t2");
    }
}
class PushCallback implements MqttCallback {


    private String threadId;
    public PushCallback(String threadId){
        this.threadId = threadId;
    }

    public void connectionLost(Throwable cause) {

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
//       System.out.println("deliveryComplete---------" + token.isComplete());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        System.out.println(topic + " " + msg);
    }
}
