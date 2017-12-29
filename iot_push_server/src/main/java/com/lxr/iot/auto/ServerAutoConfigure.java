package com.lxr.iot.auto;

import com.lxr.iot.properties.InitBean;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 自动化配置初始化服务
 *
 * @author lxr
 * @create 2017-11-29 19:52
 **/
@Configuration
@ConditionalOnClass({InitServer.class})
@EnableConfigurationProperties({InitBean.class})
public class ServerAutoConfigure {


    public ServerAutoConfigure(){

    }

    @Bean(initMethod = "open", destroyMethod = "close")
    @ConditionalOnMissingBean
    public InitServer initServer(InitBean serverBean, Environment env){
        if(!ObjectUtils.allNotNull(serverBean.getPort(),serverBean.getServerName())){
            throw  new NullPointerException("not set port");
        }
        if(serverBean.getBacklog()<1){
            serverBean.setBacklog(1024);
        }
        if(serverBean.getBossThread()<1){
            serverBean.setBossThread(Runtime.getRuntime().availableProcessors());
        }
        if(serverBean.getInitalDelay()<0){
            serverBean.setInitalDelay(10);
        }
        if(serverBean.getPeriod()<1){
            serverBean.setPeriod(10);
        }
        if(serverBean.getRead()<1){
            serverBean.setRead(120);
        }
        if(serverBean.getWrite()<1){
            serverBean.setWrite(120);
        }
        if(serverBean.getReadAndWrite()<1){
            serverBean.setReadAndWrite(120);
        }
        if(serverBean.getRevbuf()<1){
            serverBean.setRevbuf(10*1024*1024);
        }
        if(serverBean.getSndbuf()<1){
            serverBean.setSndbuf(10*1024*1024);
        }
        if(serverBean.getWorkThread()<1){
            serverBean.setWorkThread(Runtime.getRuntime().availableProcessors()*2);
        }
        return new InitServer(serverBean);
    }

}
