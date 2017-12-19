package com.lxr.iot.bootstrap.channel.mqtt.publish;

import com.lxr.iot.bootstrap.channel.mqtt.bean.ConfirmMessage;
import com.lxr.iot.bootstrap.channel.mqtt.bean.MqttChannel;
import com.lxr.iot.bootstrap.channel.mqtt.bean.WillMeaasge;
import com.lxr.iot.bootstrap.channel.mqtt.enums.QosStatus;
import com.lxr.iot.pool.Scheduled;
import com.lxr.iot.util.MessageId;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 发送消息以及确认
 *
 * @author lxr
 * @create 2017-11-24 11:04
 **/
@Slf4j
public class PublishApiSevice {


    private  Scheduled scheduled;


    public PublishApiSevice(Scheduled scheduled) {
        this.scheduled =scheduled;
    }

    /**
     * 写入遗嘱消息
     * @param willMeaasge
     */
    protected void writeWillMsg(MqttChannel mqttChannel,WillMeaasge willMeaasge) {
//        dup保证消息可靠传输，默认为0，只占用一个字节，表示第一次发送。不能用于检测消息重复发送等
        switch (willMeaasge.getQos()){
            case 0: // qos0
                sendQos0Msg(mqttChannel.getChannel(),willMeaasge.getWillTopic(),willMeaasge.getWillMessage().getBytes());
                break;
            case 1: // qos1
                sendQosConfirmMsg(MqttQoS.AT_LEAST_ONCE,mqttChannel,willMeaasge.getWillTopic(),willMeaasge.getWillMessage().getBytes());
                break;
            case 2: // qos2
                sendQosConfirmMsg(MqttQoS.EXACTLY_ONCE,mqttChannel,willMeaasge.getWillTopic(),willMeaasge.getWillMessage().getBytes());
                break;
        }

    }

    protected void sendQosConfirmMsg(MqttQoS qos, MqttChannel mqttChannel, String topic, byte[] bytes) {
        if(mqttChannel.isLogin()){
            int messageId = MessageId.messageId();
            switch (qos){
                case AT_LEAST_ONCE:
                    mqttChannel.addConfirmMsg(messageId, ConfirmMessage.builder().byteBuf(bytes).qos(MqttQoS.AT_LEAST_ONCE).topic(topic).build());
                    sendQos1Msg(mqttChannel.getChannel(),topic,false,bytes,messageId,true);
                    break;
                case EXACTLY_ONCE:
                    mqttChannel.addConfirmMsg(messageId, ConfirmMessage.builder().byteBuf(bytes).qos(MqttQoS.EXACTLY_ONCE).qosStatus(QosStatus.PUBD).topic(topic).build());
                    sendQos2Msg(mqttChannel.getChannel(),topic,false,bytes,messageId,true);
                    break;
            }

        }

    }


    /**
     * 发送 qos1 类的消息
     * @param channel
     * @param topic
     * @param byteBuf
     */
    private   void  sendQos1Msg(Channel channel, String topic,boolean isDup, byte[] byteBuf,int messageId,boolean isTime){
        log.info("PublishApiSevice sendQos1Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】发送qos1消息【topic："+topic+"】");
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,isDup, MqttQoS.AT_LEAST_ONCE,false,0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic,messageId );
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(byteBuf));
        channel.writeAndFlush(mqttPublishMessage);
        if(isTime){
            AttributeKey<ScheduledFuture> attributeKey = AttributeKey.valueOf("qos1"+messageId);
            channel.attr(attributeKey).set(scheduled.submit(() -> {
                log.info("PublishApiSevice sendQos1Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】重复发送消息【topic："+topic+"】");
                sendQos1Msg(channel,topic,true,byteBuf,messageId,false);
            }));
        }

    }



    /**
     * 发送 qos0 类的消息  byte
     * @param channel
     * @param topic
     * @param byteBuf
     */
    protected   void  sendQos0Msg(Channel channel, String topic, byte[] byteBuf){
        if(channel!=null){
            sendQos0Msg(channel,topic,byteBuf,0);
        }
    }
    private    void  sendQos0Msg(Channel channel, String topic, byte[] byteBuf,int messageId){
        log.info("成功发送消息:"+new String(byteBuf));
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,false, MqttQoS.AT_MOST_ONCE,false,0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic,messageId );
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader,Unpooled.wrappedBuffer(byteBuf));
        channel.writeAndFlush(mqttPublishMessage);
    }




    private void sendQos2Msg(Channel channel, String topic,boolean isDup, byte[] byteBuf, int messageId,boolean isTime) {
        log.info("PublishApiSevice sendQos2Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】发送qos2消息【topic："+topic+"】");
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,isDup, MqttQoS.EXACTLY_ONCE,false,0);
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic,messageId );
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader,mqttPublishVariableHeader, Unpooled.wrappedBuffer(byteBuf));
        channel.writeAndFlush(mqttPublishMessage);
        if(isTime){
            AttributeKey<ScheduledFuture> attributeKey = AttributeKey.valueOf("send_qos2"+messageId);
            channel.attr(attributeKey).set(scheduled.submit(() -> {
                log.info("PublishApiSevice sendQos2Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】重复发送消息【topic："+topic+"】");
                sendQos2Msg(channel,topic,true,byteBuf,messageId,false);
            }));
        }
    }


    /**
     * 发送qos1 publish  确认消息
     * @param channel
     * @param messageId
     */
    protected   void  sendPubBack(Channel channel,int messageId){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
    }


    /**
     * 发送qos2 publish  确认消息 第一步
     * @param channel
     * @param messageId
     */
    protected   void  sendPubRec(Channel channel,boolean isDup,int messageId,boolean isTime){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
        if(isTime){
            AttributeKey<ScheduledFuture> attributeKey = AttributeKey.valueOf("rec_qos2"+messageId);
            channel.attr(attributeKey).set(scheduled.submit(() -> {
                log.info("PublishApiSevice recQos2Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】重复 PubRec");
                sendPubRec(channel,true,messageId,false);
            }));
        }
    }

    /**
     * 发送qos2 publish  确认消息 第二步
     * @param channel
     * @param messageId
     */
    protected   void  sendPubRel(Channel channel,boolean isDup,int messageId,boolean isTime){

        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,isDup, MqttQoS.AT_LEAST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
        if(isTime){
            AttributeKey<ScheduledFuture> attributeKey = AttributeKey.valueOf("send_qos2"+messageId);
            channel.attr(attributeKey).set(scheduled.submit(() -> {
                log.info("PublishApiSevice sendQos2Msg :"+channel.remoteAddress()+"【meaasgeId:"+messageId+"】重复 pubrel");
                sendPubRel(channel,true,messageId,false);
            }));
        }
    }

    /**
     * 发送qos2 publish  确认消息 第三步
     * @param channel
     * @param messageId
     */
    protected   void  sendToPubComp(Channel channel,int messageId){
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,false, MqttQoS.AT_MOST_ONCE,false,0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader,from);
        channel.writeAndFlush(mqttPubAckMessage);
    }

}
