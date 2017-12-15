package com.lxr.iot.init;

import com.lxr.iot.bootstrap.BootstrapServer;
import com.lxr.iot.bootstrap.NettyBootstrapServer;
import com.lxr.iot.properties.ServerBean;

/**
 * 初始化服务
 *
 * @author lxr
 * @create 2017-11-29 20:12
 **/
public class InitServer {

    private ServerBean serverBean;

    public InitServer(ServerBean serverBean) {
        this.serverBean = serverBean;
    }

    BootstrapServer bootstrapServer;

    public void open(){
        if(serverBean!=null){
                    bootstrapServer = new NettyBootstrapServer();
                    bootstrapServer.setServerBean(serverBean);
                    bootstrapServer.start();
        };
    }


    public void close(){
        if(bootstrapServer!=null){
            bootstrapServer.shutdown();
        }
    }

}
