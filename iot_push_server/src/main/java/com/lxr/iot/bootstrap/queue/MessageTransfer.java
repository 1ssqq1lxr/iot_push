package com.lxr.iot.bootstrap.queue;

import com.lmax.disruptor.RingBuffer;
import com.lxr.iot.bootstrap.bean.SendMqttMessage;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageTransfer {

    private ConcurrentHashMap<String,ConcurrentHashMap<Integer, Disposable >> concurrentHashMap = new ConcurrentHashMap<>();

    @Autowired
    private  MessageStarter<MessageEvent> messageStarter;

    public  void addQueue(SendMqttMessage sendMqttMessage){
        RingBuffer<MessageEvent> ringBuffer= messageStarter.getRingBuffer();
        ConcurrentHashMap<Integer, Disposable > qos=concurrentHashMap.computeIfAbsent(sendMqttMessage.getChannel().id().toString(),(key)->{
            ConcurrentHashMap<Integer, Disposable > map=new ConcurrentHashMap<>();
            return map;
        });
        qos.put(sendMqttMessage.getMessageId(),
                Mono.fromRunnable(()->{
                         ringBuffer.publishEvent((event, sequence) -> event.setMessage(sendMqttMessage));
                         qos.remove(sendMqttMessage.getMessageId());
                        })
                        .delaySubscription(Duration.ofSeconds(10)).subscribe());
    }

    public  void removeQueue(Channel channel,Integer messageId){
        Optional.ofNullable(concurrentHashMap.get(channel.id().toString()))
                .ifPresent(map-> Optional.ofNullable(map.get(messageId))
                        .ifPresent(disposable -> {
                            disposable.dispose();
                            map.remove(messageId);
                        }));
    }





}
