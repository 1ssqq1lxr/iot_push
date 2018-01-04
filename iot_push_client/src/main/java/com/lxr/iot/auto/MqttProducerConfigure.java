package com.lxr.iot.auto;

import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.properties.ConnectOptions;
import com.lxr.iot.properties.InitBean;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自动配置类
 *
 * @author lxr
 * @create 2018-01-04 20:50
 **/
@Configuration
@ConditionalOnClass({MqttProducer.class})
@EnableConfigurationProperties({ConnectOptions.class})
public class MqttProducerConfigure   implements ApplicationContextAware, InitializingBean {


    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    @Resource
    private StandardEnvironment environment;

    @Bean(initMethod = "open", destroyMethod = "close")
    @ConditionalOnMissingBean
    public Producer initServer(ConnectOptions connectOptions, Environment env){
        MqttProducer mqttProducer = new MqttProducer();
        return mqttProducer;
    }


    @Override
    @ConditionalOnBean(Producer.class)
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
