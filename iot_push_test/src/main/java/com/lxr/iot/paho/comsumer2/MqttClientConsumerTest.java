package com.lxr.iot.paho.comsumer2;

import com.lxr.iot.ssl.SecureSocketSslContextFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;

/**
 * 测试客户端
 *
 * @author lxr
 * @create 2017-11-28 14:14
 **/
public class MqttClientConsumerTest {

    private static int qos = 0; //只有一次
    private static String broker = "ssl://127.0.0.1:1884";
    private static String userName = "tuyou";
    private static String passWord = "tuyou";


    private static MqttClient connect(String clientId, String userName,
                                          String password) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false);
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(10);
        connOpts.setKeepAliveInterval(20);
        SSLSocketFactory socketFactory = SecureSocketSslContextFactory.getClientContext().getSocketFactory();
        connOpts.setSocketFactory(socketFactory);
//      String[] uris = {"tcp://10.100.124.206:1883","tcp://10.100.124.207:1883"};
//      connOpts.setServerURIs(uris);  //起到负载均衡和高可用的作用
        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
        mqttClient.setCallback(new PushCallback("test"));
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    private static void sub(MqttClient sampleClient, String msg, String topic)
            throws MqttPersistenceException, MqttException {
        sampleClient.subscribe(topic);
    }

    private static void sub(String str,String clientId,String topic) throws MqttException{
        MqttClient mqttClient = connect(clientId,userName,passWord);

        if (mqttClient != null) {
            sub(mqttClient, str, topic);
            System.out.println(topic+"  " + str);
        }

//        if (mqttClient != null) {
//            mqttClient.connect();
//        }
    }

    public static void main(String[] args) throws MqttException {
        sub("message content","client-id-3","/t1");
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
