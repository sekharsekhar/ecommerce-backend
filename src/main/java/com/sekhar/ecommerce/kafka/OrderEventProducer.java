package com.sekhar.ecommerce.kafka;

import com.sekhar.ecommerce.config.KafkaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // send order placed event
    public void sendOrderPlacedEvent(Long orderId) {
        String message = "ORDER_PLACED:" + orderId;
        kafkaTemplate.send(KafkaConfig.ORDER_TOPIC, message);
        System.out.println("Order event sent: " + message);
    }

    // send order cancelled event
    public void sendOrderCancelledEvent(Long orderId) {
        String message = "ORDER_CANCELLED:" + orderId;
        kafkaTemplate.send(KafkaConfig.ORDER_TOPIC, message);
        System.out.println("Order cancelled event sent: " + message);
    }

    // send inventory update event
    public void sendInventoryUpdateEvent(Long productId, Integer quantity) {
        String message = "INVENTORY_UPDATED:" + productId + ":" + quantity;
        kafkaTemplate.send(KafkaConfig.INVENTORY_TOPIC, message);
        System.out.println("Inventory event sent: " + message);
    }
}