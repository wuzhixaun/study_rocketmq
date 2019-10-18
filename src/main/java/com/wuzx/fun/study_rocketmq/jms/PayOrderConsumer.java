package com.wuzx.fun.study_rocketmq.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wuzhixuan on 19-10-16.
 */
@Component
public class PayOrderConsumer {


    //消费这Topic
    private final String order_topic = "wuzx_order_topic";

    //消费者nameServer
    private String nameServer = "120.78.81.89:9876";

    //定义一个生产者组
    private String payProducterGroup = "pay_rder_Group";

    //创建消费
    private DefaultMQPushConsumer consumer;


    public PayOrderConsumer() throws MQClientException {
        //实例化
        consumer = new DefaultMQPushConsumer(payProducterGroup);

        //设置nameServer
        consumer.setNamesrvAddr(nameServer);

        //设置消息获取策略
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //设置集群方式(默认的是集群方式)
        consumer.setMessageModel(MessageModel.BROADCASTING);


        //设置订阅topic
        try {
            consumer.subscribe("1", "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //设置监听
        consumer.setMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            Message message = list.get(0);

            // System.out.printf("线程名称：%s :消费新的消息 %s",Thread.currentThread().getName(),message.getBody().toString());
            System.out.println(message.toString());
            return null;
        });


        //启动
        consumer.start();
    }


}
