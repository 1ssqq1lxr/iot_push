package com.lxr.iot.bootstrap.channel.mqtt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lxr.iot.bootstrap.BaseApi;
import com.lxr.iot.bootstrap.ChannelService;
import com.lxr.iot.bootstrap.channel.mqtt.bean.MqttChannel;
import com.lxr.iot.bootstrap.channel.mqtt.bean.RetainMessage;
import com.lxr.iot.bootstrap.channel.mqtt.cache.CacheMap;
import com.lxr.iot.bootstrap.channel.mqtt.publish.PublishApiSevice;
import com.lxr.iot.pool.Scheduled;
import com.lxr.iot.pool.StandardThreadExecutor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 抽象类
 *
 * @author lxr
 * @create 2017-12-12 20:01
 **/
@Slf4j
public abstract class AbstractChannelService extends PublishApiSevice implements ChannelService ,BaseApi {


    protected AttributeKey<Boolean> _login = AttributeKey.valueOf("login");

    protected   AttributeKey<String> _deviceId = AttributeKey.valueOf("deviceId");

    protected  static char SPLITOR = '/';

    protected StandardThreadExecutor standardThreadExecutor  ;


    protected static CacheMap<String,MqttChannel> cacheMap= new CacheMap<>();


    protected static ConcurrentHashMap<String ,MqttChannel> mqttChannels = new ConcurrentHashMap<>(); // deviceId - mqChannel 登录


    protected  static  ConcurrentHashMap<String,ConcurrentLinkedQueue<RetainMessage>> retain = new ConcurrentHashMap<>(); // topic - 保留消息



    protected  static  Cache<String, Collection<MqttChannel>> mqttChannelCache = CacheBuilder.newBuilder().maximumSize(100).build();

    public AbstractChannelService( Scheduled scheduled, StandardThreadExecutor standardThreadExecutor) {
            super(scheduled);
            this.standardThreadExecutor=standardThreadExecutor;
    }

    protected  Collection<MqttChannel> getChannels(String topic,TopicFilter topicFilter){
            try {
                Collection<MqttChannel> mqttChannels = mqttChannelCache.get(topic, () -> topicFilter.filter(topic));
                return mqttChannels;
            } catch (Exception e) {
                log.info(String.format("guava cache key topic【%s】 channel   value== null ",topic));
            }
            return null;
    }


    @FunctionalInterface
    interface TopicFilter{
        Collection<MqttChannel> filter(String topic);
    }

    protected boolean deleteChannel(String topic,MqttChannel mqttChannel){
        mqttChannelCache.invalidate(topic);
        return  cacheMap.delete(getTopic(topic),mqttChannel);
    }

    protected boolean addChannel(String topic,MqttChannel mqttChannel)
    {
        mqttChannelCache.invalidate(topic);
        return  cacheMap.putData(getTopic(topic),mqttChannel);
    }

    /**
     * 获取channel
     * @param deviceId
     * @return
     */
    public MqttChannel getMqttChannel(String deviceId){
        return mqttChannels.get(deviceId);

    }

    /**
     * 获取channelId
     * @param channel
     * @return
     */
    public String  getDeviceId(Channel channel){
        return channel.attr(_deviceId).get();
    }



    protected String[] getTopic(String topic)  {
        return StringUtils.split(topic,SPLITOR);
    }



}
