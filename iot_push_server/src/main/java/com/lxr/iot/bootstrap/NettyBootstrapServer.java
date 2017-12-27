package com.lxr.iot.bootstrap;

import com.lxr.iot.ip.IpUtils;
import com.lxr.iot.properties.ServerBean;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * netty 服务启动类
 *
 * @author lxr
 * @create 2017-11-18 14:03
 **/
@Setter
@Getter
@lombok.extern.slf4j.Slf4j
public class NettyBootstrapServer extends AbstractBootstrapServer {

    private ServerBean serverBean;

    public ServerBean getServerBean() {
        return serverBean;
    }

    public void setServerBean(ServerBean serverBean) {
        this.serverBean = serverBean;
    }

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;

    ServerBootstrap bootstrap=null ;// 启动辅助类


    public void start() {
        initEventPool();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, serverBean.isReuseaddr())
                .option(ChannelOption.SO_BACKLOG, serverBean.getBacklog())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_RCVBUF, serverBean.getRevbuf())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                                initHandler(ch.pipeline(),serverBean);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, serverBean.isKeepalive())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_SNDBUF, serverBean.getSndbuf())
                .childOption(ChannelOption.TCP_NODELAY, serverBean.isTcpNodelay());
        bootstrap.bind(serverBean.getPort()).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess())
                log.info("服务端启动成功【" + IpUtils.getHost() + ":" + serverBean.getPort() + "】");
            else
                log.info("服务端启动失败【" + IpUtils.getHost() + ":" + serverBean.getPort() + "】");
        });
    }
    /**
     * 初始化EnentPool
     */
    private void  initEventPool(){
//        Multimap multima = ImmutableMultimap.builder().build();
//        HashMultimap<Object, Object> objectObjectHashMultimap = HashMultimap.create();
        bootstrap= new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(serverBean.getBossThread(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, "BOSS_" + index.incrementAndGet());
            }
        });
        workGroup = new NioEventLoopGroup(serverBean.getWorkThread(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, "WORK_" + index.incrementAndGet());
            }
        });

    }

    /**
     * 关闭资源
     */
    public void shutdown() {
            if(workGroup!=null && bossGroup!=null ){
                try {
                    bossGroup.shutdownGracefully().sync();// 优雅关闭
                    workGroup.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    log.info("服务端关闭资源失败【" + IpUtils.getHost() + ":" + serverBean.getPort() + "】");
                }
            }
    }


}

