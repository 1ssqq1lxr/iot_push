package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.MqttApi;
import com.lxr.iot.enums.ConfirmStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描未确认信息
 *
 * @author lxr
 * @create 2018-01-06 16:50
 **/

@Slf4j
public abstract class ScanRunnable extends MqttApi implements Runnable {



    public ScanRunnable(ConcurrentLinkedQueue<SendMqttMessage> queue) {
        this.queue = queue;
    }

    private ConcurrentLinkedQueue<SendMqttMessage> queue ;


    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }


    @Override
    public void run() {
        List<SendMqttMessage> list =new LinkedList<>();
        SendMqttMessage poll ;
        for(;(poll=queue.poll())!=null;){
            if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                log.info("【消息未确认成功 "+poll.getMessageId()+":"+poll.getTopic()+"】");
                list.add(poll);
                doInfo(poll);
            }
            log.info("【消息确认成功 "+poll.getMessageId()+":"+poll.getTopic()+"】");
            break;
        }
        addQueues(list);
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
