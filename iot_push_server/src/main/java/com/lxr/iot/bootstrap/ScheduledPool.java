package com.lxr.iot.bootstrap;

import com.lxr.iot.pool.Scheduled;
import com.lxr.iot.properties.InitBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * 定时任务
 *
 * @author lxr
 * @create 2017-12-14 10:39
 **/
@Service
public class ScheduledPool implements Scheduled {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);


    private final InitBean serverBean;

    public ScheduledPool(InitBean serverBean) {
        this.serverBean = serverBean;
    }

    public   ScheduledFuture<?> submit(Runnable runnable){
        int initalDelay = serverBean.getInitalDelay();
        int period = serverBean.getPeriod();
        return scheduledExecutorService.scheduleAtFixedRate(runnable, initalDelay, period, TimeUnit.SECONDS);
    }

//    public static void main(String[] a) throws InterruptedException {
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
//        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.print("123");
//            }
//        }, 2, TimeUnit.SECONDS);
//    }

}
