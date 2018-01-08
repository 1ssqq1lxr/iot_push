package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.bootstrap.PublishApiSevice;
import com.lxr.iot.properties.ConnectOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
        for (;;){
            T poll = queue.poll();
            if(poll==null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("睡眠异常",e);
                }
            }
            else{
                // 进行处理
                doInfo(poll);
            }
        }
    }


    public  abstract  void  doInfo( T poll);


}
