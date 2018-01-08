package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.bootstrap.cache.Cache;
import com.lxr.iot.bootstrap.time.SacnScheduled;
import com.lxr.iot.bootstrap.time.ScanRunnable;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.pool.Scheduled;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * mqtt api操作类
 *
 * @author lxr
 * @create 2018-01-04 15:10
 **/
@Slf4j
public class MqttProducer  extends  AbsMqttProducer{


    @Autowired
    private SacnScheduled sacnScheduled;

    public  MqttProducer connect(ConnectOptions connectOptions){
        connectTo(connectOptions);
        return this;
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
        MqttMessage mqttMessage = buildMqttMessage(topic, message, retained, qos, false, true);
        pubMessage(channel, mqttMessage);
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(mqttMessage);
        } while (!flag);
    }

    private MqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos, boolean dup, boolean time) {
        int messageId=0;
        if(qos!=0){
            messageId=MessageId.messageId();
        }
        try {
            return MqttMessage.builder().messageId(messageId)
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
        Optional.ofNullable(getTopics(subMessages)).ifPresent(mqttTopicSubscriptions -> {
            MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptions);
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE,false, MqttQoS.AT_LEAST_ONCE,false,0);
            MqttMessageIdVariableHeader mqttMessageIdVariableHeader =MqttMessageIdVariableHeader.from(MessageId.messageId());
            MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader,mqttMessageIdVariableHeader,mqttSubscribePayload);
            channel.writeAndFlush(mqttSubscribeMessage);
        });
    }

    private List<MqttTopicSubscription> getTopics(SubMessage[] subMessages) {
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


}
