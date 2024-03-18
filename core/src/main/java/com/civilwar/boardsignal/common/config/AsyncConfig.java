package com.civilwar.boardsignal.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final static int CORE_POOL_SIZE = 3;
    private final static int MAX_POOL_SIZE = 20;
    private final static int QUEUE_CAPACITY = 100;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE); // 기본 스레드 수
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE); // 최대 스레드 수
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY); // Queue 사이즈
        taskExecutor.setThreadNamePrefix("Executor-");
        return taskExecutor;
    }
}
