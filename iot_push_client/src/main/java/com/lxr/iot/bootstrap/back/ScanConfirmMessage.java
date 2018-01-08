package com.lxr.iot.bootstrap.back;

import com.lxr.iot.bootstrap.Bean.MqttMessage;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.bootstrap.PublishApiSevice;
import com.lxr.iot.enums.ConfirmStatus;
import com.lxr.iot.properties.ConnectOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描未确认信息
 *
 * @author lxr
 * @create 2018-01-06 16:50
 **/

@Component
@Slf4j
public class ScanConfirmMessage extends PublishApiSevice implements Runnable {

    @Autowired
    private Producer producer;

    @Autowired
    private ConnectOptions connectOptions;

    private  static final ConcurrentLinkedQueue<MqttMessage> queue = new ConcurrentLinkedQueue<>();




    public static boolean addQueue(MqttMessage mqttMessage){
        return queue.add(mqttMessage);
    }


    @Override
    public void run() {

        for (;;){
            MqttMessage poll = queue.poll();
            if(poll==null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("睡眠异常",e);
                }
            }
            else{
                // 进行处理
                switch (poll.getConfirmStatus()){
                    case NO:
                        synchronized (poll){
                            if(poll.getConfirmStatus()== ConfirmStatus.NO){
                                sendQosMessage(producer.getChannel(),poll);
                                boolean flag;
                                do{
                                    flag=  ScanConfirmMessage.addQueue(poll);
                                }while (!flag);
                            }
                        }
                        break;
                    case YES:
                }

            }
        }

    }


}
