package com.lxr.iot.auto;

import com.lxr.iot.bootstrap.Bean.SendMqttMessage;
import com.lxr.iot.bootstrap.MqttProducer;
import com.lxr.iot.bootstrap.Producer;
import com.lxr.iot.bootstrap.Bean.SubMessage;
import com.lxr.iot.bootstrap.time.SacnScheduled;
import com.lxr.iot.properties.ConnectOptions;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
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
public class MqttProducerConfigure   implements ApplicationContextAware,DisposableBean {


    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    @Bean
    @ConditionalOnMissingBean()
    public Producer initServer(ConnectOptions connectOptions, Environment env){
        MqttProducer mqttProducer = new MqttProducer();
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(MqttMessageListener.class);
        MqttProducer connect = mqttProducer.connect(connectOptions);
        Optional.of(beansWithAnnotation).ifPresent(mqttListener -> {
            beansWithAnnotation.forEach((name, bean) -> {
                Class<?> clazz = AopUtils.getTargetClass(bean);
                MqttMessageListener mqttMessageListener =(MqttMessageListener) bean;
                if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
                    throw new IllegalStateException(clazz + " is not instance of " + MqttListener.class.getName());
                }
                SubMessage build = SubMessage.builder()
                        .qos(mqttMessageListener.qos())
                        .topic(mqttMessageListener.topic())
                        .build();
                mqttProducer.sub(build);

            });
        });
        ConcurrentLinkedQueue<SendMqttMessage> queue = new ConcurrentLinkedQueue<>();
        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(SacnScheduled.class);
        beanBuilder.setInitMethodName("start");
        beanBuilder.setDestroyMethodName("close");
        beanBuilder.addConstructorArgValue(queue);
        beanBuilder.addConstructorArgValue(connect);
        beanBuilder.addConstructorArgValue(connectOptions.getMinPeriod());
        String containerBeanName = String.format("%s_%s", SacnScheduled.class.getName(), counter.incrementAndGet());
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(containerBeanName, beanBuilder.getBeanDefinition());
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
