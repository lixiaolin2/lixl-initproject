package cn.xmlly.common.config.threadPool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 定时线程池
 */
@Component
@ConfigurationProperties("thread-pool.schedule")
@Data
@Slf4j
public class ScheduleThreadPoolConfig {
    private int poolSize;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(poolSize);
        executor.setThreadNamePrefix("SchedulerThread-");

        log.info("【定时线程池创建成功，poolSize{}】", poolSize);

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
