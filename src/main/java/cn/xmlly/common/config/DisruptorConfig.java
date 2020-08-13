package cn.xmlly.common.config;


import cn.xmlly.common.utils.disruptor.event.JsonEvent;
import cn.xmlly.common.utils.disruptor.event.JsonEventFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "disruptor")
@Component
@Data
public class DisruptorConfig {

//    @Autowired
//    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
//
//    private List<Integer> customerNumList;

//    @Bean("jingDongCustomerRingBuffer")
//    public RingBuffer ringBuffer(JingDongCustomer contactMessageCustomer) {
//        return initRingBuffer(contactMessageCustomer, customerNumList.get(0));
//    }
//
//    private RingBuffer initRingBuffer(WorkHandler workHandler, Integer customerNum) {
//        int bufferSize = 128 * 128;//环形队列长度，必须是2的N次方
//        EventFactory<JsonEvent> eventFactory = new JsonEventFactory();
//        Disruptor disruptor = new Disruptor(eventFactory, bufferSize, threadPoolTaskExecutor.getThreadPoolExecutor().getThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());
//
//        WorkHandler[] workHandlers=new WorkHandler[customerNum];
//        for(int i = 0; i < customerNum; i++) {
//            workHandlers[i]=workHandler;
//        }
//
//        disruptor.handleEventsWithWorkerPool(workHandlers);
//        RingBuffer roomRingBuffer = disruptor.start();
//        return roomRingBuffer;
//    }
}
