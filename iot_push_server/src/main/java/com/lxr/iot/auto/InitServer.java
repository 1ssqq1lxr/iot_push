package com.lxr.iot.auto;

import com.lxr.iot.bootstrap.BootstrapServer;
import com.lxr.iot.bootstrap.NettyBootstrapServer;
import com.lxr.iot.bootstrap.bean.SendMqttMessage;
import com.lxr.iot.bootstrap.scan.ScanRunnable;
import com.lxr.iot.properties.InitBean;

/**
 * 初始化服务
 *
 * @author lxr
 * @create 2017-11-29 20:12
 **/
public class InitServer {

    private InitBean serverBean;

    public InitServer(InitBean serverBean) {
        this.serverBean = serverBean;
    }

    BootstrapServer bootstrapServer;

    public void open(){
        if(serverBean!=null){
            bootstrapServer = new NettyBootstrapServer();
            bootstrapServer.setServerBean(serverBean);
            bootstrapServer.start();
        }
    }


    public void close(){
        if(bootstrapServer!=null){
            bootstrapServer.shutdown();
        }
    }

}
