package com.lemon.gmail.bootuserserviceprovider.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.lemon.gmail.bootuserserviceprovider.service.impl.UserServiceImpl;
import com.lemon.gmail.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-10 15:48
 **/
@Configuration
public class MyDubboConfig {
    /*<dubbo:application name="user-service-provider"></dubbo:application>*/
    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("user-service-provider");
        return applicationConfig;
    }
    /*<dubbo:registry protocol="zookeeper" address="121.199.70.188:2181"></dubbo:registry>*/
    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("121.199.70.188:2181");
        return registryConfig;
    }
    /*<dubbo:protocol name="dubbo" port="20880"></dubbo:protocol>*/
    @Bean
    public ProtocolConfig protocolConfig(){
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(20880);
        return protocolConfig;
    }
    /* <dubbo:service interface="com.lemon.gmail.service.UserService" ref="userServiceImpl"></dubbo:service>*/
    @Bean
    public ServiceConfig<UserService> userServiceConfig(UserService userService){
        ServiceConfig<UserService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(UserService.class);
        serviceConfig.setRef(userService);
        return serviceConfig;
    }
}
