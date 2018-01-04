package com.lxr.iot.bootstrap;

import lombok.Data;

/**
 * 订阅消息
 *
 * @author lxr
 * @create 2018-01-04 19:42
 **/
@Data
public class SubMessage {

    private String topic;

    private  int  qos;

}
