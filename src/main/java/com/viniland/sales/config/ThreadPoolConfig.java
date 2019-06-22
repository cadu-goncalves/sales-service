package com.viniland.sales.config;

import com.viniland.sales.component.ServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Bootstrap thread pool features.
 */
@Configuration
public class ThreadPoolConfig {

    @Autowired
    private ServiceProperties properties;

    /**
     * Factory for thread pool used to support async execution.
     *
     * @return {@link TaskExecutor}
     */
    @Bean
    public TaskExecutor futureExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // Pool bounds
        taskExecutor.setCorePoolSize(properties.getThreads().getMin());
        taskExecutor.setMaxPoolSize(properties.getThreads().getMax());
        // Log friendly
        taskExecutor.setThreadGroupName("API-");
        // Initialize and return
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
