package com.viniland.sales.component.kafka;

import com.viniland.sales.component.ServiceProperties;
import com.viniland.sales.domain.event.CashbackCreditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Emits cashback credits to external microservices
 */
@Component
@Slf4j
public class CashbackCreditProducer {

    private final ServiceProperties properties;

    private final KafkaTemplate<String, CashbackCreditEvent> template;

    public CashbackCreditProducer(ServiceProperties properties, KafkaTemplate<String, CashbackCreditEvent> template) {
        this.properties = properties;
        this.template = template;
    }

    public void send(CashbackCreditEvent event) {
        String topic = properties.getTopics().get("cashback");
        log.info("Publish to topic {} event {}", topic, event);
        this.template.send(topic, event);
    }

}