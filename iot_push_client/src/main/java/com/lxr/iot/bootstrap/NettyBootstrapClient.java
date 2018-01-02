package com.lxr.iot.bootstrap;

import com.lxr.iot.ip.IpUtils;
import com.lxr.iot.properties.InitBean;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * bootstarp 启动类
 *
 * @author lxr
 * @create 2017-12-21 15:55
 **/
@Slf4j
public class NettyBootstrapClient extends AbstractBootstrapClient {


    private NioEventLoopGroup bossGroup;


    Bootstrap bootstrap=null ;// 启动辅助类

    private final  InitBean initBean;

    public NettyBootstrapClient(InitBean initBean) {
        this.initBean = initBean;
    }

    @Override
    public void start() {
        initEventPool();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, initBean.isTcpNodelay())
                .option(ChannelOption.SO_KEEPALIVE, initBean.isKeepalive())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_SNDBUF, initBean.getSndbuf())
                .option(ChannelOption.SO_RCVBUF, initBean.getRevbuf())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                                    initHandler(ch.pipeline(),initBean);
                    }
                });
        ChannelFuture connect = bootstrap.connect(initBean.getServerIp(), initBean.getPort());
        connect.addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                log.info("客户端连接成功【" + IpUtils.getHost() + ":" + initBean.getPort() + "】");
            }
            else {
                log.info("客户端连接失败【" + IpUtils.getHost() + ":" + initBean.getPort() + "】");
            }
        });
    }

    @Override
    public void shutdown() {
        if( bossGroup!=null ){
            try {
                bossGroup.shutdownGracefully().sync();// 优雅关闭
            } catch (InterruptedException e) {
                log.info("客户端关闭资源失败【" + IpUtils.getHost() + ":" + initBean.getPort() + "】");
            }
        }
    }

    @Override
    public void initEventPool() {
        bootstrap= new Bootstrap();
        bossGroup = new NioEventLoopGroup(4, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            public Thread newThread(Runnable r) {
                return new Thread(r, "BOSS_" + index.incrementAndGet());
            }
        });
    }
}
