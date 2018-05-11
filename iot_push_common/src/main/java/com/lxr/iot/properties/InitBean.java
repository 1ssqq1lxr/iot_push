package com.lxr.iot.properties;

import com.lxr.iot.enums.ProtocolEnum;
import com.lxr.iot.mqtt.MqttHander;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * netty服务启动参数
 *
 * @author lxr
 * @create 2017-11-18 15:08
 **/
@ConfigurationProperties(prefix ="lxr.iot.server")
@Data
public class InitBean {

    private ProtocolEnum protocol;

    private int port ;

    private String serverName ;

    private boolean keepalive ;

    private boolean reuseaddr ;


    private boolean tcpNodelay ;

    private int backlog ;

    private  int  sndbuf ;

    private int revbuf ;


    private int heart ;

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
