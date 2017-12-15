package com.lxr.iot.init;

import com.lxr.iot.properties.ServerBean;
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
@EnableConfigurationProperties({ServerBean.class})
public class ServerAutoConfigure {


    public ServerAutoConfigure(){

    }

    @Bean(initMethod = "open", destroyMethod = "close")
    @ConditionalOnMissingBean
    public InitServer initServer(ServerBean serverBean, Environment env){
        return new InitServer(serverBean);
    }

}
