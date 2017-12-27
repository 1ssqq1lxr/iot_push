package com.lxr.iot.properties;

import com.lxr.iot.bootstrap.hander.mqtt.MqttHander;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 客户端bean
 *
 * @author lxr
 * @create 2017-12-21 16:19
 **/
@Component
@ConfigurationProperties(prefix ="lxr.iot.client")
@Data
public class ClientBean {

    private String serverHost;

    private int port;

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



    private int  initalDelay ;

    private  int period ;

    private Class<MqttHander> mqttHander ;

    private int bossThread;

    private int workThread;

}
