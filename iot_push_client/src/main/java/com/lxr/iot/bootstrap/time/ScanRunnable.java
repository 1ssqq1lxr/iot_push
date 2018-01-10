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

    private List<SendMqttMessage> list = Collections.synchronizedList(new LinkedList<>());

    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }


    @Override
    public void run() {
        SendMqttMessage poll ;
        for(;(poll=queue.poll())!=null;){
            doInfo(poll);
            if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                list.add(poll);
            }
        }
        addQueues(list);
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
