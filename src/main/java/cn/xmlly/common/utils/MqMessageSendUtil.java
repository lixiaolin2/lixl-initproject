package cn.xmlly.common.utils;

import cn.xmlly.common.config.mq.activemq.QueueProducer;
import cn.xmlly.common.config.mq.rabbitmq.RabbitmqAsyncSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dyman
 * @describe 队列消息发送工具
 * @date 2019/9/9
 */
@Slf4j
@Component("mqMessageSendUtil")
public class MqMessageSendUtil {

    @Value("${mq.activemq.isopen}")
    private boolean activemqIsOpen;

    @Value("${mq.rabbitmq.isopen}")
    private boolean rabbitmqIsOpen;
    /** 交换机前缀 */
    private String DEFAULT_EXCHANGE_PREFIX = "exchange.";

    /**
     * 有状态异步发送消息到指定的队列
     * @param queueName 队列缓存的key
     * @param serverId
     * @param message
     */
    public void asyncSend(String queueName, String serverId, final Object message) {
        if(activemqIsOpen) {
            QueueProducer queueAsyncSender = ApplicationContextUtils.getBean("queueAsyncSender");
            queueAsyncSender.sendMapMessage(queueName + serverId, message);
        }

        if(rabbitmqIsOpen) {
            RabbitmqAsyncSender rabbitmqAsyncSender = ApplicationContextUtils.getBean("rabbitmqAsyncSender");
            String queueNamePrefix = queueName;
            String exchange = DEFAULT_EXCHANGE_PREFIX + (queueNamePrefix.endsWith("_") ? queueNamePrefix.substring(0, queueNamePrefix.length() - 1) : queueNamePrefix);
            rabbitmqAsyncSender.send(exchange, queueNamePrefix + serverId, message);
        }
    }

    /**
     * 异步发送一条消息到指定的队列（目标）
     * @User 好未来
     * @param queueName 队列名
     * @param message
     */
    public void asyncSendString(String queueName, String message) {
        if(activemqIsOpen) {
            QueueProducer queueAsyncSender = ApplicationContextUtils.getBean("queueAsyncSender");
            queueAsyncSender.sendString(queueName, message);
        }

        if(rabbitmqIsOpen) {
            RabbitmqAsyncSender rabbitmqAsyncSender = ApplicationContextUtils.getBean("rabbitmqAsyncSender");
            String exchange = DEFAULT_EXCHANGE_PREFIX + queueName;
            rabbitmqAsyncSender.send(exchange, queueName, message);
        }
    }


}
