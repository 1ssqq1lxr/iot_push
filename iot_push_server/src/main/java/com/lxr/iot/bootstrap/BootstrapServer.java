package com.lxr.iot.bootstrap;

import com.lxr.iot.properties.InitBean;

/**
 * 启动类接口
 *
 * @author lxr
 * @create 2017-11-18 14:05
 **/
public interface BootstrapServer {

    void shutdown();

    void setServerBean(InitBean serverBean);


}
