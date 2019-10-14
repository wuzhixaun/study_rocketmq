package com.wuzx.fun.study_rocketmq.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wuzhixuan on 19-10-13.
 */
@Component
public class PayConsumer {


    private String consumer_gruop = "pay_Group";

    private String nameServerAddr = "120.78.81.89:9876";
    private String topic = "pay-topic";



    //创建默认消费者对象
    private DefaultMQPushConsumer mqPushConsumer;

    public PayConsumer() throws MQClientException {
        mqPushConsumer = new DefaultMQPushConsumer(consumer_gruop);

        mqPushConsumer.setNamesrvAddr(nameServerAddr);

        //设置获取消息策略
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //设置订阅主题
        mqPushConsumer.subscribe(topic, "*");
        //设置监听器
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (messageExtList, consumeConcurrentlyContext) -> {

            try {
                Message message = messageExtList.get(0);

                String topic = message.getTopic();
                String body = new String(message.getBody(), "utf-8");

                String keys = message.getKeys();
                String tags = message.getTags();

                System.out.println("topic:"+topic+"body:"+body+"keys"+keys+"tags:"+tags);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

        });

        mqPushConsumer.start();

    }
}
