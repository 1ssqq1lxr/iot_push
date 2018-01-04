package com.lxr.iot.bootstrap;

import com.lxr.iot.properties.ConnectOptions;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 操作类
 *
 * @author lxr
 * @create 2018-01-04 17:23
 **/
@Slf4j
public abstract class AbsMqttProducer implements  Producer {


    protected   Channel channel;

    private  static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public  void  connectTo(ConnectOptions connectOptions){
        NettyBootstrapClient nettyBootstrapClient = new NettyBootstrapClient(connectOptions);
        this.channel =nettyBootstrapClient.start();
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("等待链接超时",e);
        }
    }

    public  void connectBack(MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch ( mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                countDownLatch.countDown();
                 break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                throw new RuntimeException("用户名密码错误");
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                throw  new RuntimeException("clientId  不允许链接");
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                throw new RuntimeException("服务不可用");
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                throw new RuntimeException("mqtt 版本不可用");
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                throw new RuntimeException("未授权登录");
        }

    }

}
