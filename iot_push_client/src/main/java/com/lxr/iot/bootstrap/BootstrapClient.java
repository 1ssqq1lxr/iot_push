package com.lxr.iot.bootstrap;


import io.netty.channel.Channel;

/**
 * 启动类接口
 *
 * @author lxr
 * @create 2017-11-18 14:05
 **/
public interface BootstrapClient {


    void shutdown();

    void initEventPool();

    Channel start();


}
