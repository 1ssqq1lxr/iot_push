package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.PublishApiSevice;
import com.lxr.iot.enums.ConfirmStatus;
import lombok.extern.slf4j.Slf4j;

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
public abstract class ScanRunnable extends PublishApiSevice implements Runnable {



    public ScanRunnable(ConcurrentLinkedQueue<MqttMessage> queue) {
        this.queue = queue;
    }

    private ConcurrentLinkedQueue<MqttMessage> queue ;

    public  boolean addQueue(MqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<MqttMessage> ts){
        return queue.addAll(ts);
    }


    @Override
    public void run() {
        List<MqttMessage> list = new LinkedList<>();
        MqttMessage poll ;
        for(;(poll=queue.poll())!=null;){
            doInfo(poll);
            if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                list.add(poll);
            }
        }
        addQueues(list);
    }
    public  abstract  void  doInfo( MqttMessage poll);


}
