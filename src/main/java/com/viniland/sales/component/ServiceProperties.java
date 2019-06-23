package com.viniland.sales.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties("service")
public class ServiceProperties {

    private ThreadPool threads = new ThreadPool();

    private Map<String, String> topics = new HashMap<>();

    @Data
    public static class ThreadPool {

        private int min;

        private int max;
    }

    @PostConstruct
    void postConstruct() {
        if (threads.max > 500 || threads.max <= 0) {
            threads.max = 30;
        }

        if (threads.min > 500 || threads.min <= 0) {
            threads.min = 10;
        }

        if (threads.min > threads.max) {
            threads.min = threads.max;
        }
    }
}
