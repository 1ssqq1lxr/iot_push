package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * mqtt api操作类
 *
 * @author lxr
 * @create 2018-01-04 15:10
 **/
@Slf4j
public class MqttProducer  extends  AbsMqttProducer{



    public  Producer connect(ConnectOptions connectOptions){
        connectTo(connectOptions);
        initPool(new ConcurrentLinkedQueue(),connectOptions.getMinPeriod());
        return this;
    }

    @Override
    protected void initPool(ConcurrentLinkedQueue queue, int seconds) {
        super.initPool(queue, seconds);
    }

    @Override
    public void pub(String topic,String message,int qos){
        pub(topic,message,false,qos);
    }

    @Override
    public void pub(String topic, String message, boolean retained) {
        pub(topic,message,retained,0);
    }
    @Override
    public void pub(String topic,String message){
        pub(topic,message,false,0);
    }


    @Override
    public void pub(String topic, String message, boolean retained, int qos) {
        SendMqttMessage mqttMessage = buildMqttMessage(topic, message, retained, qos, false, true);
        pubMessage(channel, mqttMessage);
    }

    private SendMqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos, boolean dup, boolean time) {
        int messageId=0;
        if(qos!=0){
            messageId=MessageId.messageId();
        }
        try {
            return SendMqttMessage.builder().messageId(messageId)
                    .Topic(topic)
                    .dup(dup)
                    .retained(retained)
                    .qos(qos)
                    .confirmStatus(ConfirmStatus.PUB)
                    .timestamp(System.currentTimeMillis())
                    .payload(message.getBytes("Utf-8")).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sub(SubMessage... subMessages){
        Optional.ofNullable(getSubTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            int messageId = MessageId.messageId();
            subMessage(channel, mqttTopicSubscriptions,messageId);
            topics.addAll(mqttTopicSubscriptions);
        });
    }

    @Override
    public void unsub(SubMessage... subMessages) {
        Optional.ofNullable(getSubTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            int messageId = MessageId.messageId();
            subMessage(channel, mqttTopicSubscriptions,messageId);
            topics.addAll(mqttTopicSubscriptions);
        });
    }

    @Override
    public void disConnect() {

    }

    private List<MqttTopicSubscription> getSubTopics(SubMessage[] subMessages) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<MqttTopicSubscription> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        MqttTopicSubscription mqttTopicSubscription  = new MqttTopicSubscription(sb.getTopic(),sb.getQos());
                        mqttTopicSubscriptions.add(mqttTopicSubscription);
                    }
                    return mqttTopicSubscriptions;
                }).orElseGet(null);
    }


    private List<String> getTopics(SubMessage[] subMessages) {
        return  Optional.ofNullable(subMessages)
                .map(subMessages1 -> {
                    List<String> mqttTopicSubscriptions = new LinkedList<>();
                    for(SubMessage sb :subMessages1){
                        mqttTopicSubscriptions.add(sb.getTopic());
                    }
                    return mqttTopicSubscriptions;
                }).orElseGet(null);
    }




}
