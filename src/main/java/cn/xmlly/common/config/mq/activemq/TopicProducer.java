package cn.xmlly.common.config.mq.activemq;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * 订阅模式提供者
 *
 * @author : caigq
 * @version : 1.0
 * @date : 2018-05-24 9:21
 */
@ConditionalOnProperty(
        value = {"mq.activemq.isopen"},
        matchIfMissing = false
)
@Component
@Slf4j
public class TopicProducer {

    @Autowired
    private JmsTemplate jmsTopicTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void sendMapMessage(String topicName, Object message) {
        threadPoolTaskExecutor.submit(() -> {
            try {
                log.info("【topic-->send】:activeCount={},queueCount={},completedTaskCount={},taskCount={}", threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size(), threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount(), threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());

                Destination destination = new ActiveMQTopic(topicName);
                // 这里定义了Queue的key
                ActiveMQMapMessage mqMapMessage = new ActiveMQMapMessage();

                mqMapMessage.setJMSDestination(destination);
                mqMapMessage.setObject("msg", message);
                this.jmsTopicTemplate.convertAndSend(destination, mqMapMessage);
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("【TopicProducer Exception】:{}", e);
            }
        });
    }
}
