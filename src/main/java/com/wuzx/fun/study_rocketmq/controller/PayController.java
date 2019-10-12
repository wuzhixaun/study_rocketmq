package com.wuzx.fun.study_rocketmq.controller;

import com.wuzx.fun.study_rocketmq.jms.PayProducter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pay")
public class PayController {

    //创建Topic
    private static final String topic = "pay-topic";

    @Autowired
    private PayProducter producter;

    @RequestMapping("/api/producter")
    public Object callBack(String text) {
        Message message = new Message(topic, "pay_producter", ("wuzx" + text).getBytes());
        SendResult sendResult =null;
        try {
            sendResult = producter.getMqProducerInstance().send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sendResult;
    }
}
