package com.viniland.sales.config;

import com.viniland.sales.component.ServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Bootstrap thread pool features.
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * Factory for thread pool used to support async execution.
     *
     * @param serviceProperties {@link ServiceProperties}
     * @return {@link TaskExecutor}
     */
    @Bean
    public TaskExecutor futureExecutor(ServiceProperties serviceProperties) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // Pool bounds
        taskExecutor.setCorePoolSize(serviceProperties.getThreads().getMin());
        taskExecutor.setMaxPoolSize(serviceProperties.getThreads().getMax());
        // Log friendly
        taskExecutor.setThreadGroupName("API-");
        // Initialize and return
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
