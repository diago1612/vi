package com.ibs.vi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {
    @Bean(name = "viSegmentExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(24);
        executor.setMaxPoolSize(48);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("segment-");
        executor.initialize();
        return executor;
    }
}
