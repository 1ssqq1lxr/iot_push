package com.lxr.iot.bootstrap.time;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.properties.ConnectOptions;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 定时产生工场
 *
 * @author lxr
 * @create 2018-01-08 13:04
 **/
public class TimeProduceFactory {

    public static  Runnable getQos0Runnable(ConcurrentLinkedQueue<MqttMessage> t,Producer producer){
        return new ScanRunnable<MqttMessage>(t) {
            @Override
            public void doInfo(MqttMessage poll) {
                    switch (poll.getConfirmStatus()){
                        case PUB:
                            pubMessage(producer.getChannel(),poll);
                            boolean flag;
                            do{
                                flag=  this.addQueue(poll);
                            }while (!flag);
                            break;
                        case PUBREC:
                            break;
                        case PUBREL:
                            break;
                        case COMPLETE:
                            break;
                    }
            }
        };
    }

}
