package com.wuzx.fun.study_rocketmq.jms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Created by wuzhixuan on 19-10-13.
 */
@Data
@Configuration
@PropertySource(value = "classpath:jmsconfig.properties")
@ConfigurationProperties("jms")
public class JMSConfig {
    //创建Topic
    private  String topic ;

    //定义一个生产者组
    private String payProducterGroup ;

    //定义一个nameServerAddr
    private String nameServerAddr ;

}
