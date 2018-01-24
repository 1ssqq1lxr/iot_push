package com.lxr.iot.bootstrap.scan;

import com.lxr.iot.bootstrap.bean.SendMqttMessage;
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
public abstract class ScanRunnable  implements Runnable {



    private ConcurrentLinkedQueue<SendMqttMessage> queue  = new ConcurrentLinkedQueue<>();


    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }



    @Override
    public void run() {
        for(;;){
            if(!queue.isEmpty()){
                List<SendMqttMessage> list =new LinkedList<>();
                SendMqttMessage poll ;
                for(;(poll=queue.poll())!=null;){
                    if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                        list.add(poll);
                        doInfo(poll);
                    }
                    break;
                }
                addQueues(list);
            }
        }
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
