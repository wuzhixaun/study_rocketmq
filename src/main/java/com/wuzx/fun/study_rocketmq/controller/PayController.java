package com.wuzx.fun.study_rocketmq.controller;

import com.wuzx.fun.study_rocketmq.domain.ProductOrder;
import com.wuzx.fun.study_rocketmq.jms.JMSConfig;
import com.wuzx.fun.study_rocketmq.jms.PayProducter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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



    @Autowired
    private JMSConfig jmsConfig;
    @RequestMapping("/api/testJmsConfig")
    public Object testJmsConfig() {
        return jmsConfig.toString();
    }


    @RequestMapping(value = "/api/orderProductMes")
    public Object orderProductMes() throws Exception {
        //获取订单
        List<ProductOrder> productOrderList = ProductOrder.getProductOrderList();

        //创建生产者对象
        DefaultMQProducer mqProducer = producter.getMqProducerInstance();


        List<SendResult> sendResults = new ArrayList<>();

        for (ProductOrder order : productOrderList) {

            //创建消息
            Message message = new Message("wuzx_order_topic", order.getType(), order.getOrderId() + "", order.toString().getBytes());

            SendResult sendResult = mqProducer.send(message, (mqs, msg, arg) -> {
                Long orderId = (long) arg;
                long index = orderId % mqs.size();

                return mqs.get((int) index);

            }, order.getOrderId());

            sendResults.add(sendResult);
        }


        return sendResults;



    }
}
