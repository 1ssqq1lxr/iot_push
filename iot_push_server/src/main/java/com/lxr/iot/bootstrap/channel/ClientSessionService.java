package com.lxr.iot.bootstrap.channel;

import com.lxr.iot.bootstrap.bean.SessionMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 会话保留处理
 *
 * @author lxr
 * @create 2017-11-23 11:21
 **/
@Service
public class ClientSessionService {

    private static ConcurrentHashMap<String,ConcurrentLinkedQueue<SessionMessage>>  queueSession  = new ConcurrentHashMap<>();  // 连接关闭后 保留此session 数据  deviceId


    public  void saveSessionMsg(String deviceId, SessionMessage sessionMessage) {
        ConcurrentLinkedQueue<SessionMessage> sessionMessages = queueSession.getOrDefault(deviceId, new ConcurrentLinkedQueue<>());
        boolean flag;
        do{
             flag = sessionMessages.add(sessionMessage);
        }
        while (!flag);
        queueSession.put(deviceId,sessionMessages);
    }

    public  ConcurrentLinkedQueue<SessionMessage> getByteBuf(String deviceId){
        return   Optional.ofNullable(deviceId).map(s -> queueSession.get(s))
                .orElse(null);
    }
}
