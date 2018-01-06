package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

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
        MqttMessage   mqttMessage=  buildMqttMessage(topic,message,retained,qos,false,true);
        Optional.ofNullable(mqttMessage).ifPresent(mqttMessage1 -> {
            switch (MqttQoS.valueOf(mqttMessage1.getQos())){
                case AT_MOST_ONCE:
                    sendQos0(channel,mqttMessage1);
                    break;
                case AT_LEAST_ONCE:
                    sendQos1(channel,mqttMessage1);
                    break;
                case EXACTLY_ONCE:
            }
        });

    }

    private MqttMessage buildMqttMessage(String topic, String message, boolean retained, int qos,boolean dup,boolean time) {
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
