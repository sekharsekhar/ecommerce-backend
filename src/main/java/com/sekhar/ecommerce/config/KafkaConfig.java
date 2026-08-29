package com.sekhar.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // topic names
    public static final String ORDER_TOPIC = "order-events";
    public static final String INVENTORY_TOPIC = "inventory-events";
    public static final String NOTIFICATION_TOPIC = "notification-events";

    @Bean
    public NewTopic orderTopic() {
        return new NewTopic(ORDER_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic inventoryTopic() {
        return new NewTopic(INVENTORY_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic(NOTIFICATION_TOPIC, 1, (short) 1);
    }
}