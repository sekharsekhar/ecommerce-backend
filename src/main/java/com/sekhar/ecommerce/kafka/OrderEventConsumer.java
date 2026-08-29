package com.sekhar.ecommerce.kafka;

import com.sekhar.ecommerce.config.KafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    // listen to order events
    @KafkaListener(topics = KafkaConfig.ORDER_TOPIC,
            groupId = "ecommerce-group")
    public void handleOrderEvent(String message) {
        System.out.println("Order event received: " + message);

        if (message.startsWith("ORDER_PLACED:")) {
            Long orderId = Long.parseLong(message.split(":")[1]);
            System.out.println("Processing order: " + orderId);
            // send notification, update analytics etc
        }

        if (message.startsWith("ORDER_CANCELLED:")) {
            Long orderId = Long.parseLong(message.split(":")[1]);
            System.out.println("Order cancelled: " + orderId);
            // restore inventory, send notification etc
        }
    }

    // listen to inventory events
    @KafkaListener(topics = KafkaConfig.INVENTORY_TOPIC,
            groupId = "ecommerce-group")
    public void handleInventoryEvent(String message) {
        System.out.println("Inventory event received: " + message);

        if (message.startsWith("INVENTORY_UPDATED:")) {
            String[] parts = message.split(":");
            Long productId = Long.parseLong(parts[1]);
            Integer quantity = Integer.parseInt(parts[2]);
            System.out.println("Inventory updated for product: "
                    + productId + " quantity: " + quantity);
        }
    }
}