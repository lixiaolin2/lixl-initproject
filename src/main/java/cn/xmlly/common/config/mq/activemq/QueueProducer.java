package cn.xmlly.common.config.mq.activemq;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import java.io.Serializable;

/**
 * 队列模式提供者
 */
@Slf4j
@ConditionalOnProperty(
        value = {"mq.activemq.isopen"},
        matchIfMissing = false
)
@Component("queueAsyncSender")
public class QueueProducer {

    /**
     * MQ jms实例
     **/
    @Autowired
    private JmsTemplate jmsQueueTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void sendMapMessage(String queueName, Object message) {
        try {
            Destination destination = new ActiveMQQueue(queueName);
            // 这里定义了Queue的key
            ActiveMQMapMessage mqMapMessage = new ActiveMQMapMessage();
            mqMapMessage.setJMSDestination(destination);
            mqMapMessage.setObject("result", message);
            this.jmsQueueTemplate.convertAndSend(destination, mqMapMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendObjectMessage(String queueName, Object message) {
        try {
            log.info("【queue-->send】:activeCount={},queueCount={},completedTaskCount={},taskCount={}", threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size(), threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());
            log.info("请求:{}", message.toString());
            Destination destination = new ActiveMQQueue(queueName);
            // 这里定义了Queue的key
            ActiveMQObjectMessage mqObjectMessage = new ActiveMQObjectMessage();
            mqObjectMessage.setJMSDestination(destination);
            mqObjectMessage.setObject((Serializable) message);
            this.jmsQueueTemplate.convertAndSend(destination, mqObjectMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendObjectMessage(Destination destination, Object message) {
        try {
            // 这里定义了Queue的key
            log.info("【queue-->send:activeCount={},queueCount={},completedTaskCount={},taskCount={}】", threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size(), threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());

            ActiveMQObjectMessage mqObjectMessage = new ActiveMQObjectMessage();
            mqObjectMessage.setJMSDestination(destination);
            mqObjectMessage.setObject((Serializable) message);
            this.jmsQueueTemplate.convertAndSend(destination, mqObjectMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送一条消息到指定的队列（目标）
     * @param queueName 队列名称
     * @param message   消息内容
     * @throws Exception ex
     */
    @SneakyThrows
    public void sendString(String queueName, final String message) {
        Destination destination = new ActiveMQQueue(queueName);
        log.debug("【异步消息发送:queueName={}】", queueName);
        ActiveMQObjectMessage mqObjectMessage = new ActiveMQObjectMessage();
        mqObjectMessage.setObject((Serializable) message);
        mqObjectMessage.setStringProperty("Content_Type", "text/plain");

        this.jmsQueueTemplate.convertAndSend(destination, mqObjectMessage);
    }
}
