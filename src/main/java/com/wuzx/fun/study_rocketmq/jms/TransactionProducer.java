package com.wuzx.fun.study_rocketmq.jms;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class TransactionProducer {


    //设置事务生产者Group
    private String transaction_group = "transaction_group";

    //定义一个nameServerAddr
    private String nameServerAddr = "120.78.81.89:9876";


    //事务生产者对象
    private TransactionMQProducer mqProducer = null;

    /**
     * 自定义线程池
     * @param corePoolSize     核心池大小 int
     * @param maximumPoolSize  最大池大小 int
     * @param keepAliveTime    保活时间   long（任务完成后要销毁的延时）
     * @param unit             时间单位    决定参数3的单位，枚举类型的时间单位
     * @param workQueue        工作队列    用于存储任务的工作队列（BlockingQueue接口类型）
     * @param threadFactory    线程工厂    用于创建线程
     */
    private ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("client_transaction_mq");
            return thread;
        }
    });


    public TransactionProducer() {
        mqProducer = new TransactionMQProducer(transaction_group);

        //设置nameServer
        mqProducer.setNamesrvAddr(nameServerAddr);

        //设置线程池
        mqProducer.setExecutorService(executorService);

        //设置事务监听器

        mqProducer.setTransactionListener(new TransactionListenImpl());


        this.start();
    }

    /**
     * 返回事务生产者对象
     * @return
     */
    public TransactionMQProducer getInstance() {
        return mqProducer;
    }


    /**
     * 消费者和生产这对象每次使用之前都需要调用，只能初始化一次
     */
    public void start() {
        try {
            mqProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    class TransactionListenImpl implements TransactionListener {

        /*执行本地事务*/
        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            System.out.println("----------executeLocalTransaction-----------");
            String body = new String(msg.getBody());
            String key = msg.getKeys();
            String transactionId = msg.getTransactionId();

            System.out.println("body：" + body + " key:" + key + " transactionId:" + transactionId);

            //执行本地事务start



            //执行本地事务end



            //二次消费确定
            int status = Integer.parseInt(arg.toString());
            if (status == 1) {
                return  LocalTransactionState.COMMIT_MESSAGE;
            } else if (status == 2) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            } else {
                return LocalTransactionState.UNKNOW;
            }

        }

        /*消息回查*/
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            System.out.println("----------checkLocalTransaction-----------");

            String body = new String(msg.getBody());
            String key = msg.getKeys();
            String transactionId = msg.getTransactionId();

            //这里只可以返回commit或者Rollback


            //根据key检查本地消息是否完成

            return null;
        }
    }
}
