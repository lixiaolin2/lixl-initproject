package cn.xmlly.common.config.mq.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author dyman
 * @describe rabbitmq异步消息发送器
 * @date 2019/9/9
 */
@ConditionalOnProperty(
        value = {"mq.rabbitmq.isopen"},
        matchIfMissing = false
)
@Slf4j
@Component("rabbitmqAsyncSender")
public class RabbitmqAsyncSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchangeName 交换机
     * @param routingKey 路由键
     * @param object 消息内容
     */
    public void send(String exchangeName, String routingKey, Object object) {
        if (exchangeName != null && routingKey != null) {
            log.info("【rabbitmq异步消息发送: exchangeName={}, routingKey={}】", exchangeName, routingKey);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, object);
            log.info("【rabbitmq异步消息发送结束时间:={}, exchangeName={}, routingKey={}】", new Date(), exchangeName, routingKey);
        } else {
            log.warn("【rabbitmq异步消息发送异常:  exchangeName={}, routingKey={}】", exchangeName, routingKey);
        }
    }
}
