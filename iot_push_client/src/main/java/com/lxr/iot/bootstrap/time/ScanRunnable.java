package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.PublishApiSevice;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描未确认信息
 *
 * @author lxr
 * @create 2018-01-06 16:50
 **/

@Slf4j
public abstract class ScanRunnable<T> extends PublishApiSevice implements Runnable {



    public ScanRunnable(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    private ConcurrentLinkedQueue<T> queue ;

    public  boolean addQueue(T t){
        return queue.add(t);
    }


    @Override
    public void run() {
        T poll ;
        System.out.print("===========================");
        for(;(poll=queue.poll())!=null;){
            doInfo(poll);
        }
    }
    public  abstract  void  doInfo( T poll);


}
