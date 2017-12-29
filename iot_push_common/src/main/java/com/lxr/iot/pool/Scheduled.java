package com.lxr.iot.pool;

import java.util.concurrent.ScheduledFuture;

/**
 * 接口
 *
 * @author lxr
 * @create 2017-12-14 10:47
 **/
public interface Scheduled {

    ScheduledFuture<?> submit(Runnable runnable);
}
