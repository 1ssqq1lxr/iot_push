package com.lxr.iot.properties;

import com.lxr.iot.bootstrap.handler.mqtt.MqttHander;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * netty服务启动参数
 *
 * @author lxr
 * @create 2017-11-18 15:08
 **/
@Component
@ConfigurationProperties(prefix ="lxr.iot.server")
@Data
public class ServerBean {

    private int port =8088;


    private String serverName ="defaultServer";

    private boolean keepalive =true;

    private boolean reuseaddr =true;


    private boolean tcpNodelay =true;

    private int backlog =1024;

    private  int  sndbuf =10*1024*1024;

    private int revbuf =10*1024*1024;


    private int read =60;

    private int write =60;

    private int readAndWrite =60;

    private boolean ssl =false;

    private Class<MqttHander> mqttHander ;

    private int  initalDelay =2;

    private  int period =2;

}
