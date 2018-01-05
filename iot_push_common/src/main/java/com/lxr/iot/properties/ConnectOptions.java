package com.lxr.iot.properties;

import com.lxr.iot.mqtt.MqttHander;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 链接参数配置
 *
 * @author lxr
 * @create 2018-01-04 16:13
 **/
@ConfigurationProperties(prefix ="lxr.iot.client")
@Data
public class ConnectOptions {

    private Long connectTime;

    private String serverIp;

    private int port ;

    private boolean keepalive ;

    private boolean reuseaddr ;


    private boolean tcpNodelay ;

    private int backlog ;

    private  int  sndbuf ;

    private int revbuf ;


    private boolean ssl ;

    private String jksFile;

    private String jksStorePassword;

    private String jksCertificatePassword;

    private Class<MqttHander> mqttHander ;


    private int  initalDelay ;

    private  int period ;

    private int bossThread;

    private int workThread;

    private MqttOpntions mqtt;

    @Data
    public static class MqttOpntions{

        private  String clientIdentifier;

        private  String willTopic;

        private  String willMessage;

        private  String userName;

        private  String password;

        private  String name;

        private  boolean hasUserName;

        private  boolean hasPassword;

        private  boolean isWillRetain;

        private  int willQos;

        private  boolean isWillFlag;

        private  boolean isCleanSession;

        private  int keepAliveTimeSeconds;

    }
}
