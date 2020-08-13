package cn.xmlly.common.config.mq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.DeliveryMode;
import javax.jms.Destination;

/**
 * Class Description
 * Author: lixingjia
 * Date: 2019-01-22
 * Time: 16:54
 */
@ConditionalOnProperty(
        value = {"mq.activemq.isopen"},
        matchIfMissing = false
)
@Configuration
public class ActiveMqConfig {

    @Autowired
    private Environment env;

    @Bean
    @Primary
    public ActiveMQConnectionFactory producerAmqConnectionFactory() {
        ActiveMQConnectionFactory item = new ActiveMQConnectionFactory();
        item.setBrokerURL(env.getProperty("mq.activemq.producer.brokerURL"));
        item.setUserName(env.getProperty("mq.activemq.producer.userName"));
        item.setPassword(env.getProperty("mq.activemq.producer.password"));
        //关闭安全检查，并相信所有类
        item.setTrustAllPackages(true);
        //如果需要信任部分包则使用下面的配置
//        item.setTrustedPackages(new ArrayList(Arrays.asList("org.apache.activemq.test,org.apache.camel.test".split(","))));
        return item;
    }

    @Bean
    public CachingConnectionFactory producerConnectionFactory(@Qualifier("producerAmqConnectionFactory") ActiveMQConnectionFactory producerAmqConnectionFactory) {
        CachingConnectionFactory item = new CachingConnectionFactory();
        item.setTargetConnectionFactory(producerAmqConnectionFactory);
        item.setSessionCacheSize(100);
        return item;
    }

    @Bean
    public ActiveMQConnectionFactory consumerAmqConnectionFactory() {
        ActiveMQConnectionFactory item = new ActiveMQConnectionFactory();
        item.setBrokerURL(env.getProperty("mq.activemq.consumer.brokerURL"));
        item.setUserName(env.getProperty("mq.activemq.consumer.userName"));
        item.setPassword(env.getProperty("mq.activemq.consumer.password"));
        item.setTrustAllPackages(true);
        return item;
    }

    @Bean
    public CachingConnectionFactory consumerConnectionFactory(@Qualifier("consumerAmqConnectionFactory") ActiveMQConnectionFactory consumerAmqConnectionFactory) {
        CachingConnectionFactory item = new CachingConnectionFactory();
        item.setTargetConnectionFactory(consumerAmqConnectionFactory);
        item.setSessionCacheSize(100);
        return item;
    }

    /**
     * Spring JmsTemplate 的消息生产者 定义JmsTemplate的Queue类型
     *
     * @param producerAmqConnectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsQueueTemplate(@Qualifier("producerAmqConnectionFactory") ActiveMQConnectionFactory producerAmqConnectionFactory) {
        JmsTemplate item = new JmsTemplate();
        item.setConnectionFactory(producerAmqConnectionFactory);
        item.setPubSubDomain(false);
        QosSettings qosSettings = new QosSettings();
        qosSettings.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        item.setQosSettings(qosSettings);
        return item;
    }

    /**
     * Spring JmsTemplate 的消息生产者 定义JmsTemplate的Topic类型
     *
     * @param producerAmqConnectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsTopicTemplate(@Qualifier("producerAmqConnectionFactory") ActiveMQConnectionFactory producerAmqConnectionFactory) {
        JmsTemplate item = new JmsTemplate();
        item.setConnectionFactory(producerAmqConnectionFactory);
        item.setPubSubDomain(true);
        QosSettings qosSettings = new QosSettings();
        qosSettings.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        item.setQosSettings(qosSettings);
        return item;
    }

    //队列接收
    @Bean(name = "jmsQueueListener")
    public DefaultJmsListenerContainerFactory jmsQueueListener(@Qualifier("consumerConnectionFactory")CachingConnectionFactory consumerConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(consumerConnectionFactory);
        factory.setConnectionFactory(cachingConnectionFactory);
        //sessionTransacted 关闭事务提高消费者的性能，生产者开启事务提高性能
        factory.setSessionTransacted(false);
        //设置ACK为自动确认
        factory.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        //设置消费者数量
        factory.setConcurrency("5");
        DestinationResolver destinationResolver = (session, destinationName, pubSubDomain) -> {
            Destination destination = session.createQueue(destinationName);
            return destination;
        };
        factory.setDestinationResolver(destinationResolver);
        return factory;
    }

    //队列接收
    @Bean(name = "jmsQueueListenerNoClient")
    public DefaultJmsListenerContainerFactory jmsQueueListenerNoClient(@Qualifier("consumerConnectionFactory")CachingConnectionFactory consumerConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(consumerConnectionFactory);
        factory.setConnectionFactory(cachingConnectionFactory);
        //sessionTransacted 关闭事务提高消费者的性能，生产者开启事务提高性能
        factory.setSessionTransacted(false);
        //设置ACK为自动确认
        factory.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        //设置消费者数量
        factory.setConcurrency("5");
        DestinationResolver destinationResolver = (session, destinationName, pubSubDomain) -> {
            Destination destination = session.createQueue(destinationName);
            return destination;
        };
        factory.setDestinationResolver(destinationResolver);
        return factory;
    }

    //主题接收
    @Bean(name = "jmsTopicListener")
    public DefaultJmsListenerContainerFactory jmsTopicListener(@Qualifier("consumerConnectionFactory")CachingConnectionFactory consumerConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(consumerConnectionFactory);
        factory.setConnectionFactory(cachingConnectionFactory);
        //订阅模式的消费者必须开自启动
        factory.setAutoStartup(true);
        //sessionTransacted 关闭事务提高消费者的性能，生产者开启事务提高性能
        factory.setSessionTransacted(false);
        //设置为订阅模式
        factory.setPubSubDomain(true);
        //设置ACK为自动确认
        factory.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        return factory;
    }
}
