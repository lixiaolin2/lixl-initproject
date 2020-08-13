package cn.xmlly.common.config.threadPool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 普通线程池
 */
@Data
@Component
@Slf4j
@ConfigurationProperties("thread-pool.pool")
@EnableAsync
public class ThreadPoolConfig {
    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveSeconds;

    private int queueCapacity;

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //  完成任务自动关闭 , 默认为false
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //  核心线程超时退出，默认为false
        executor.setAllowCoreThreadTimeOut(true);
        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列容量
        executor.setQueueCapacity(queueCapacity);
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //线程名字前缀
        executor.setThreadNamePrefix("Thread-");

        log.info("【线程池创建成功corePoolSize={}，maxPoolSize={}，keepAliveSeconds={}，queueCapacity={}】", corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity);

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
