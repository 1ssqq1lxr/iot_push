package com.lxr.iot.bootstrap;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.cache.Cache;
import com.lxr.iot.bootstrap.channel.mqtt.MqttHandlerServiceService;
import com.lxr.iot.bootstrap.handler.mqtt.DefaultMqttHandler;
import com.lxr.iot.bootstrap.time.SacnScheduled;
import com.lxr.iot.ip.IpUtils;
import com.lxr.iot.properties.ConnectOptions;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 操作类
 *
 * @author lxr
 * @create 2018-01-04 17:23
 **/
@Slf4j
public abstract class AbsMqttProducer extends MqttApi implements  Producer {

    protected   Channel channel;

    protected static  MqttListener mqttListener;

    private  NettyBootstrapClient nettyBootstrapClient ;

    protected SacnScheduled sacnScheduled;

    protected   List<String> topics = new CopyOnWriteArrayList<>();


    private  static final CountDownLatch countDownLatch = new CountDownLatch(1);

    protected   void  connectTo(ConnectOptions connectOptions){
        this.nettyBootstrapClient= new NettyBootstrapClient(connectOptions);
        this.channel =nettyBootstrapClient.start();
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            nettyBootstrapClient.doubleConnect(); // 重新连接
        }
    }

    @Override
    protected void pubMessage(Channel channel, SendMqttMessage mqttMessage) {
        super.pubMessage(channel, mqttMessage);
        if(mqttMessage.getQos()!=0){
            Cache.put(mqttMessage.getMessageId(),mqttMessage);
            boolean flag;
            do {
                flag = sacnScheduled.addQueue(mqttMessage);
            } while (!flag);
        }
    }

    protected void initPool(ConcurrentLinkedQueue queue, int seconds){
        this.sacnScheduled =new SacnScheduled(queue,this.channel,seconds);
        sacnScheduled.start();
    }

    @Override
    protected int subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions) {
        return super.subMessage(channel, mqttTopicSubscriptions);
    }

    @Override
    public void close() {
        if(nettyBootstrapClient!=null){
            nettyBootstrapClient.shutdown();
        }
        if(sacnScheduled!=null){
            sacnScheduled.close();
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

//    private  reSub(){
//
//    }



    public class NettyBootstrapClient extends AbstractBootstrapClient {

        private NioEventLoopGroup bossGroup;

        Bootstrap bootstrap=null ;// 启动辅助类

        private ConnectOptions connectOptions;


        public NettyBootstrapClient(ConnectOptions connectOptions) {
            this.connectOptions = connectOptions;
        }


        public void doubleConnect(){
            ChannelFuture connect = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort());
            connect.addListener((ChannelFutureListener) future -> {
                    Thread.sleep(2000);
                    if (future.isSuccess())
                        AbsMqttProducer.this.channel =future.channel();
                    else
                        doubleConnect();
            });
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
                            initHandler(ch.pipeline(),connectOptions,new DefaultMqttHandler(connectOptions,new MqttHandlerServiceService(), AbsMqttProducer.this, mqttListener
                            ));
                        }
                    });
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

    public NettyBootstrapClient getNettyBootstrapClient() {
        return nettyBootstrapClient;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setMqttListener(MqttListener mqttListener) {
        this.mqttListener = mqttListener;
    }


}
