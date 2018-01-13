package com.lxr.iot.bootstrap.scan;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.pool.Scheduled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 扫描消息确认
 *
 * @author lxr
 * @create 2018-01-08 19:22
 **/
@Data
@Slf4j
public class SacnScheduled extends ScanRunnable {

    private Channel channel;

    private  ScheduledFuture<?> submit;

    private  int  seconds;

    public SacnScheduled(ConcurrentLinkedQueue queue,Channel channel,int seconds) {
        super(queue);
        this.channel=channel;
        this.seconds=seconds;
    }

    public  void start(){
        Scheduled  scheduled = new ScheduledPool();
        this.submit = scheduled.submit(this);
    }

    public  void close(){
        if(submit!=null && !submit.isCancelled()){
            submit.cancel(true);
        }
    }


    @Override
    public void doInfo(SendMqttMessage poll) {
        if(channel.isActive()){
            if(checkTime(poll)){
                poll.setTimestamp(System.currentTimeMillis());
                switch (poll.getConfirmStatus()){
                    case PUB:
                        poll.setDup(true);
                        pubMessage(channel,poll);
                        break;
                    case PUBREC:
                        sendAck(MqttMessageType.PUBREC,true,channel,poll.getMessageId());
                        break;
                    case PUBREL:
                        sendAck(MqttMessageType.PUBREL,true,channel,poll.getMessageId());
                        break;
                }

            }
        }
        else
        {
            log.info("channel is not alived");
            submit.cancel(true);
        }
    }

    private boolean checkTime(SendMqttMessage poll) {
        return System.currentTimeMillis()-poll.getTimestamp()>=seconds*1000;
    }


    static class ScheduledPool implements Scheduled {
        private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        public ScheduledFuture<?> submit(Runnable runnable){
            return scheduledExecutorService.scheduleAtFixedRate(runnable,2,2, TimeUnit.SECONDS);
        }


    }

}
