package com.lxr.iot.bootstrap;

import com.lxr.iot.properties.ServerBean;
import com.lxr.iot.ssl.SecureSocketKeyStore;
import com.lxr.iot.ssl.SecureSocketSslContextFactory;
import com.lxr.iot.ssl.SecureSokcetTrustManagerFactory;
import com.lxr.iot.util.SpringBeanUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.jboss.netty.util.internal.SystemPropertyUtil;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyStore;

/**
 * 抽象类 负责加载edec handler
 *
 * @author lxr
 * @create 2017-11-20 13:46
 **/
public abstract class AbstractBootstrapServer implements BootstrapServer {


    private   String PROTOCOL = "TLS";

    private   SSLContext SERVER_CONTEXT;


//    private   SSLContext CLIENT_CONTEXT;

    /**
     *
     * @param channelPipeline  channelPipeline
     * @param serverBean  服务配置参数
     */
    protected  void initHandler(ChannelPipeline channelPipeline,ServerBean serverBean){
        if(serverBean.isSsl()){
            if(ObjectUtils.allNotNull(serverBean.getJksCertificatePassword(),serverBean.getJksFile(),serverBean.getJksStorePassword())){
                throw  new NullPointerException("SSL file and password is null");
            }
            initSsl(serverBean);
            SSLEngine engine =
                    SERVER_CONTEXT.createSSLEngine();
            engine.setUseClientMode(false);
            channelPipeline.addLast("ssl", new SslHandler(engine));
        }
        channelPipeline.addLast("decoder", new MqttDecoder());
        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
        channelPipeline.addLast(new IdleStateHandler(serverBean.getRead(), serverBean.getWrite(), serverBean.getReadAndWrite()));
        channelPipeline.addLast(  SpringBeanUtils.getBean(serverBean.getMqttHander()));

    }


    private void initSsl(ServerBean serverBean){
        String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        SSLContext clientContext;
        try {
            //
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(  SecureSocketSslContextFactory.class.getResourceAsStream(serverBean.getJksFile()),
                    serverBean.getJksStorePassword().toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks,serverBean.getJksCertificatePassword().toCharArray());

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the server-side SSLContext", e);
        }

//        try {
//            clientContext = SSLContext.getInstance(PROTOCOL);
//            clientContext.init(null, SecureSokcetTrustManagerFactory.getTrustManagers(), null);
//        } catch (Exception e) {
//            throw new Error(
//                    "Failed to initialize the client-side SSLContext", e);
//        }

        SERVER_CONTEXT = serverContext;
//        CLIENT_CONTEXT = clientContext;
    }
}
