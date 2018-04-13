package com.lxr.iot.spring;

import com.lxr.iot.bootstrap.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lxr
 * @create 2018-04-13 17:02
 **/
@Component
public class springTest {

    @Autowired
    private Producer producer;
    /**
     *  系统初始化自动配置了，直接注入此类进行操作
     */

}
