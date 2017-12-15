package com.lxr.iot.bootstrap;

import com.lxr.iot.bootstrap.BootstrapServer;
import com.lxr.iot.properties.ServerBean;
import com.lxr.iot.ssl.SecureSocketSslContextFactory;
import com.lxr.iot.util.SpringBeanUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLEngine;

/**
 * 抽象类 负责加载edec handler
 *
 * @author lxr
 * @create 2017-11-20 13:46
 **/
public abstract class AbstractBootstrapServer implements BootstrapServer {


    /**
     *
     * @param channelPipeline  channelPipeline
     * @param serverBean  服务配置参数
     */
    protected  void initHandler(ChannelPipeline channelPipeline,ServerBean serverBean){
        if(serverBean.isSsl()){
            SSLEngine engine =
                    SecureSocketSslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            channelPipeline.addLast("ssl", new SslHandler(engine));
        }
        channelPipeline.addLast("decoder", new MqttDecoder());
        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
        channelPipeline.addLast(new IdleStateHandler(serverBean.getRead(), serverBean.getWrite(), serverBean.getReadAndWrite()));
        channelPipeline.addLast(  SpringBeanUtils.getBean(serverBean.getMqttHander()));

    }

}
