package com.wuzx.fun.study_rocketmq.jms;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

@Component
public class PayProducter {

    //定义一个生产者组
    private String payProducterGroup = "pay_Group";

    //定义一个nameServerAddr
    private String nameServerAddr = "120.78.81.89:9876";


    //消费者对象
    private DefaultMQProducer mqProducer;


    /**
     * 构造方法初始化 消费者对象，设置nameServerAddr地址，启动对象
     */
    public PayProducter() {
        mqProducer = new DefaultMQProducer(payProducterGroup);

        //设置nameServerAddr
        mqProducer.setNamesrvAddr(nameServerAddr);


        //每次使用对象都需要启动
        this.start();
    }


    public DefaultMQProducer getMqProducerInstance() {
        return this.mqProducer;
    }


    /**
     * 消费者对象启动方法
     */
    public void start() {
        try {
            mqProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

    /**
     * 消费这对象关闭，一般监听上下文
     */
    public void shutDown() {
        mqProducer.shutdown();
    }

}
