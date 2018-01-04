package com.lxr.iot.bootstrap;

import com.lxr.iot.ip.IpUtils;
import com.lxr.iot.properties.ConnectOptions;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

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

    private ConnectOptions connectOptions;


    public NettyBootstrapClient(ConnectOptions connectOptions) {
        this.connectOptions = connectOptions;
    }

    @Override
    public Channel start() {
        initEventPool();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, connectOptions.isTcpNodelay())
                .option(ChannelOption.SO_KEEPALIVE, connectOptions.isKeepalive())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_SNDBUF, connectOptions.getSndbuf())
                .option(ChannelOption.SO_RCVBUF, connectOptions.getRevbuf())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                                    initHandler(ch.pipeline(),connectOptions);
                    }
                });
        ChannelFuture connect = null;
        try {
            return bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort()).sync().channel();
        } catch (InterruptedException e) {
            log.info("connect to channel fail ");
        }
        return null;
    }

    @Override
    public void shutdown() {
        if( bossGroup!=null ){
            try {
                bossGroup.shutdownGracefully().sync();// 优雅关闭
            } catch (InterruptedException e) {
                log.info("客户端关闭资源失败【" + IpUtils.getHost() + ":" + connectOptions.getPort() + "】");
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
