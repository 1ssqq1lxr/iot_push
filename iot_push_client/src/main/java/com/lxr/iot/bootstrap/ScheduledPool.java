package com.lxr.iot.bootstrap;

import com.lxr.iot.pool.Scheduled;
import com.lxr.iot.properties.ConnectOptions;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 *
 * @author lxr
 * @create 2017-12-14 10:39
 **/
public class ScheduledPool implements Scheduled {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    private final ConnectOptions connectOptions;

    public ScheduledPool(ConnectOptions connectOptions) {
        this.connectOptions = connectOptions;
    }

    public   ScheduledFuture<?> submit(Runnable runnable){
        return scheduledExecutorService.schedule(runnable,connectOptions.getMinPeriod(), TimeUnit.SECONDS);
    }


}
