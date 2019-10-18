package com.wuzx.fun.study_rocketmq.controller;

import com.wuzx.fun.study_rocketmq.domain.ProductOrder;
import com.wuzx.fun.study_rocketmq.jms.JMSConfig;
import com.wuzx.fun.study_rocketmq.jms.PayProducter;
import com.wuzx.fun.study_rocketmq.jms.TransactionProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
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


        /**
         * 设置延迟消息发送
         *
         * messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
         *
         * 0对应的就是第一个，以此类推
         */
        message.setDelayTimeLevel(4);//30秒后才可以消费消息


        SendResult sendResult = null;
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


    @RequestMapping(value = "/api/asyncproducter")
    public Object asyncRroducerSendMessage(String mesBody) throws Exception {


        Message message = new Message(topic, "producter_tag", "asyncproducter", ("asyncproducter" + mesBody).getBytes());
//        producter.getMqProducerInstance().send(message, new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                System.out.println("消息发送成功");
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                System.out.println("异常");
//                throwable.printStackTrace();
//            }
//        });


         producter.getMqProducerInstance().send(message, (messageQueueList, message1, arg) ->
                        messageQueueList.get((Integer) arg), "4",
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.println("消息发送成功");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        System.out.println("出现异常了");
//                throwable.printStackTrace();
                    }
                });

        return null;
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

    @Autowired
    private TransactionProducer transactionProducer;

    @RequestMapping(value = "/api/sendTranscationMSG")
    public Object sendTranscationMSG(String tag, String otherparm) throws MQClientException {

        Message message = new Message("tran"+topic,tag,tag+"key",tag.getBytes());



        TransactionMQProducer transactionMQProducer = transactionProducer.getInstance();
        TransactionSendResult transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, otherparm);

        return transactionSendResult;
    }
}
