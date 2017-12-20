package com.lxr.iot.properties;

import com.lxr.iot.bootstrap.handler.mqtt.MqttHander;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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

    private String serverName ;

    private boolean keepalive ;

    private boolean reuseaddr ;


    private boolean tcpNodelay ;

    private int backlog ;

    private  int  sndbuf ;

    private int revbuf ;


    private int read ;

    private int write ;

    private int readAndWrite ;

    private boolean ssl ;

    private String jksFile;

    private String jksStorePassword;

    private String jksCertificatePassword;

    private Class<MqttHander> mqttHander ;


    private int  initalDelay ;

    private  int period ;

    private int bossThread;

    private int workThread;

}
