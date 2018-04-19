package com.lxr.iot.bootstrap;

import com.lxr.iot.auto.MqttListener;
import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.cache.Cache;
import com.lxr.iot.bootstrap.channel.MqttHandlerServiceService;
import com.lxr.iot.bootstrap.handler.DefaultMqttHandler;
import com.lxr.iot.bootstrap.scan.SacnScheduled;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.ip.IpUtils;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.util.MessageId;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
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

    protected  MqttListener mqttListener;

    private  NettyBootstrapClient nettyBootstrapClient ;

    protected SacnScheduled sacnScheduled;

    protected List<MqttTopicSubscription> topics = new ArrayList<>();


    private static  final  int _BLACKLOG =   1024;

    private static final  int  CPU =Runtime.getRuntime().availableProcessors();

    private static final  int  SEDU_DAY =10;

    private static final  int TIMEOUT =120;

    private static final  int BUF_SIZE=10*1024*1024;




    private  static final CountDownLatch countDownLatch = new CountDownLatch(1);


    protected   void  connectTo(ConnectOptions connectOptions){
        checkConnectOptions(connectOptions);
        if(this.nettyBootstrapClient ==null){
            this.nettyBootstrapClient = new NettyBootstrapClient(connectOptions);
        }
        this.channel =nettyBootstrapClient.start();
        initPool(connectOptions.getMinPeriod());
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("InterruptedException",e);
            nettyBootstrapClient.doubleConnect(); // 重新连接
        }
    }

    @Override
    public void disConnect() {
        sendDisConnect(channel);
    }

    @Override
    public void pubRecMessage(Channel channel, int messageId) {
        SendMqttMessage sendMqttMessage= SendMqttMessage.builder().messageId(messageId)
                .confirmStatus(ConfirmStatus.PUBREC)
                .timestamp(System.currentTimeMillis())
                .build();
        Cache.put(messageId,sendMqttMessage);
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(sendMqttMessage);
        } while (!flag);

        super.pubRecMessage(channel, messageId);
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

    protected void initPool( int seconds){
        this.sacnScheduled =new SacnScheduled(this,seconds);
        sacnScheduled.start();
    }



    @Override
    protected void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                subMessage(channel, mqttTopicSubscriptions, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        super.subMessage(channel, mqttTopicSubscriptions, messageId);
    }

    public void unsub(List<String> topics,int messageId) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                unSubMessage(channel, topics, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        unSubMessage(channel, topics, messageId);
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
                log.error("login error", new RuntimeException("用户名密码错误"));
                break;
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                log.error("login error", new RuntimeException("clientId  不允许链接"));
                break;
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                log.error("login error",  new RuntimeException("服务不可用"));
                break;
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                log.error("login error",  new RuntimeException("mqtt 版本不可用"));
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                log.error("login error", new RuntimeException("未授权登录"));
                break;
        }

    }

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
                if (future.isSuccess()){
                    AbsMqttProducer absMqttProducer = AbsMqttProducer.this;
                    absMqttProducer.channel =future.channel();
                    absMqttProducer.subMessage(future.channel(),topics, MessageId.messageId());
                }
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
                            initHandler(ch.pipeline(),connectOptions,new DefaultMqttHandler(connectOptions,new MqttHandlerServiceService(), AbsMqttProducer.this, mqttListener));
                        }
                    });
            try {
                return bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort()).sync().channel();
            } catch (Exception e) {
                log.info("connect to channel fail ",e);
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

    private void checkConnectOptions(ConnectOptions connectOptions){
        ConnectOptions.MqttOpntions mqtt=connectOptions.getMqtt();
        Optional.ofNullable(mqtt.getPassword()).ifPresent(s -> mqtt.setHasPassword(true));
        Optional.ofNullable(mqtt.getUserName()).ifPresent(s -> mqtt.setHasUserName(true));
        Optional.ofNullable(mqtt.getWillTopic()).ifPresent(s -> mqtt.setHasWillFlag(true));
    }
}
