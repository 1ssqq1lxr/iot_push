package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

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

    public void pub(String topic,MqttMessage mqttMessage){
        switch (MqttQoS.valueOf(mqttMessage.getQos())){
            case AT_MOST_ONCE:
                sendQos0(topic,channel,mqttMessage);
                break;
            case AT_LEAST_ONCE:
                sendQos1(channel,topic,mqttMessage,false,true);
                break;
            case EXACTLY_ONCE:
        }
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
