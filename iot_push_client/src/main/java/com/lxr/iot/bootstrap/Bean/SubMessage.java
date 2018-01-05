package com.lxr.iot.bootstrap.Bean;

import lombok.Builder;
import lombok.Data;

/**
 * 订阅消息
 *
 * @author lxr
 * @create 2018-01-04 19:42
 **/
@Builder
@Data
public class SubMessage {

    private String topic;

    private  int  qos;

}
