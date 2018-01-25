package com.lxr.iot.bootstrap;

import com.lxr.iot.mqtt.MqttHander;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.ssl.SecureSokcetTrustManagerFactory;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * 抽象类
 *
 * @author lxr
 * @create 2017-12-21 15:56
 **/
public abstract class AbstractBootstrapClient implements  BootstrapClient {


    private SSLContext CLIENT_CONTEXT;

    private   String PROTOCOL = "TLS";

    /**
     *  @param channelPipeline  channelPipeline
     * @param clientBean  客户端配置参数
     */
    protected  void initHandler(ChannelPipeline channelPipeline, ConnectOptions clientBean, MqttHander mqttHander){
        if(clientBean.isSsl()){
            initSsl();
            SSLEngine engine =
                    CLIENT_CONTEXT.createSSLEngine();
            engine.setUseClientMode(true);
            channelPipeline.addLast("ssl", new SslHandler(engine));
        }
        channelPipeline.addLast("decoder", new MqttDecoder());
        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
        channelPipeline.addLast(new IdleStateHandler(clientBean.getHeart(),0,0));
        channelPipeline.addLast(mqttHander);

    }

    private void initSsl(){
        SSLContext clientContext;
        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, SecureSokcetTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
        }
        CLIENT_CONTEXT = clientContext;
    }
}
