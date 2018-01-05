package com.lxr.iot.auto;

import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.bootstrap.SubMessage;
import com.lxr.iot.bootstrap.channel.mqtt.MqttListener;
import com.lxr.iot.bootstrap.channel.mqtt.MqttMessageListener;
import com.lxr.iot.properties.ConnectOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * 自动配置类
 *
 * @author lxr
 * @create 2018-01-04 20:50
 **/
@Configuration
@ConditionalOnClass({MqttProducer.class})
@EnableConfigurationProperties({ConnectOptions.class})
public class MqttProducerConfigure   implements ApplicationContextAware,DisposableBean {


    private ConfigurableApplicationContext applicationContext;


    @ConditionalOnMissingBean
    public Producer initServer(ConnectOptions connectOptions, Environment env){
        MqttProducer mqttProducer = new MqttProducer();
        MqttListener bean = applicationContext.getBean(MqttListener.class);
        mqttProducer.connect(connectOptions);
        Optional.of(bean).ifPresent(mqttListener -> {
            MqttMessageListener mqttMessageListener;
            if((mqttMessageListener=mqttListener.getClass().getAnnotation(MqttMessageListener.class))!=null){
                SubMessage build = SubMessage.builder()
                        .qos(mqttMessageListener.qos().value())
                        .topic(mqttMessageListener.topic())
                        .build();
                mqttProducer.sub(build);

            }
        });
        return mqttProducer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        Producer bean = applicationContext.getBean(Producer.class);
        if(bean!=null){
            bean.close();
        }
    }
}
