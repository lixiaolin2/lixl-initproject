
package cn.xmlly.common.config.mq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;


/**
 *
 * @author: dyman
 * @describe RabbitMQ配置类
 * @date: 2019/9/9
 */
@Configuration
@ConditionalOnProperty(
        value = {"mq.rabbitmq.isopen"},
        matchIfMissing = false
)
public class RabbitMqConfig {

    @Autowired
    private Environment env;

    /**
     * 创建消息发送组件
     * @return
     */
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("rabbitmqProducerConnectionFactory") ConnectionFactory rabbitmqProducerConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitmqProducerConnectionFactory);
        //exchange根据路由键匹配不到对应的queue时将会调用basic.return将消息返还给生产者
        rabbitTemplate.setMandatory(false);
        return rabbitTemplate;
    }


    @Bean
    public RabbitAdmin rabbitAdmin(@Qualifier("rabbitmqProducerConnectionFactory") ConnectionFactory rabbitmqProducerConnectionFactory) {
        //对queue、exchange、binding的声明管理
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitmqProducerConnectionFactory);
        //autoStartup设置true，否则Spring容器不会加载RabbitAdmin类
        rabbitAdmin.setAutoStartup(true);
        //声明对queue异常时忽略异常，继续声明其它的queue
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

    /**
     * 创建生产者连接工厂
     * @return
     */
    @Bean(name = "rabbitmqProducerConnectionFactory")
    @Primary
    public ConnectionFactory rabbitmqProducerConnectionFactory() {
        //创建连接工厂
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //设置集群方式
        connectionFactory.setAddresses(env.getProperty("mq.rabbitmq.producer.addresses"));
        //设置用户名
        connectionFactory.setUsername(env.getProperty("mq.rabbitmq.producer.userName"));
        //设置密码
        connectionFactory.setPassword(env.getProperty("mq.rabbitmq.producer.password"));
        //设置虚拟主机
        connectionFactory.setVirtualHost(env.getProperty("mq.rabbitmq.producer.virtualHost"));
        return connectionFactory;
    }

    /**
     * 创建消费者连接工厂
     * @return
     */
    @Bean(name = "rabbitmqConsumerConnectionFactory")
    public ConnectionFactory rabbitmqConsumerConnectionFactory() {
        //创建连接工厂
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //设置集群方式
        connectionFactory.setAddresses(env.getProperty("mq.rabbitmq.consumer.addresses"));
        //设置用户名
        connectionFactory.setUsername(env.getProperty("mq.rabbitmq.consumer.userName"));
        //设置密码
        connectionFactory.setPassword(env.getProperty("mq.rabbitmq.consumer.password"));
        //设置虚拟主机
        connectionFactory.setVirtualHost(env.getProperty("mq.rabbitmq.consumer.virtualHost"));
        return connectionFactory;
    }

    /**
     * 消费者监听
     *
     * @return
     */
    @Bean(name = "simpleRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitmqConsumerConnectionFactory());
        factory.setMessageConverter(new SimpleMessageConverter());
        //单台并发消费者数量
        factory.setConcurrentConsumers(5);
        //预取消费数量,unacked数量超过这个值broker将不会接收消息
        factory.setPrefetchCount(2);
        //消息确认机制
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
}
