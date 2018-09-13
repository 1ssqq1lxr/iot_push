package com.lxr.iot.bootstrap.scan;

import com.lxr.iot.bootstrap.bean.SendMqttMessage;
import com.lxr.iot.enums.ConfirmStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 扫描未确认信息
 *
 * @author lxr
 * @create 2018-01-06 16:50
 **/

@Slf4j
public abstract class ScanRunnable  implements Runnable {


    LinkedBlockingQueue<SendMqttMessage> queue =new LinkedBlockingQueue();

    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }


    @Override
    public void run() {
        for(;;){
            try {
                SendMqttMessage  poll= queue.take();
                if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                    doInfo(poll);
                    queue.offer(poll);
                }
            } catch (InterruptedException e) {
                log.error("scan InterruptedException",e);
            }
        }
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
