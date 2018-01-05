package com.lxr.iot.bootstrap.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 遗嘱消息
 *
 * @author lxr
 * @create 2017-11-23 15:08
 **/
@Builder
@Data
public class WillMeaasge {

    private String willTopic;

    private String willMessage;


    private  boolean isRetain;

    private int qos;

}
