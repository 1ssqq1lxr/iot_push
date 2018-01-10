package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * mqtt api操作类
 *
 * @author lxr
 * @create 2018-01-04 15:10
 **/
@Slf4j
public class MqttProducer  extends  AbsMqttProducer{


    private static final AttributeKey key  =  AttributeKey.valueOf("topic");


    public  MqttProducer connect(ConnectOptions connectOptions){
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
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(mqttMessage);
        } while (!flag);
    }

    private SendMqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos, boolean dup, boolean time) {
        int messageId=0;
        if(qos!=0){
            messageId=MessageId.messageId();
        }
        try {
            return SendMqttMessage.builder().messageId(messageId)
                    .time(time)
                    .Topic(topic)
                    .dup(dup)
                    .retained(retained)
                    .qos(qos)
                    .confirmStatus(ConfirmStatus.PUB)
                    .payload(message.getBytes("Utf-8")).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sub(SubMessage... subMessages){
        Optional.ofNullable(getSubTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            subMessage(channel,mqttTopicSubscriptions);
        });
        Optional.ofNullable(getTopics(subMessages)).ifPresent(strings -> {
            List<String> topics = (List<String>) channel.attr(key).get();
            if(CollectionUtils.isEmpty(topics)){
                channel.attr(key).set(strings);
            }
            else{
                topics.addAll(strings);
            }
        });
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
