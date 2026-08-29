package com.sekhar.ecommerce.service;

import com.sekhar.ecommerce.model.Inventory;
import com.sekhar.ecommerce.repository.InventoryRepository;
import com.sekhar.ecommerce.kafka.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderEventProducer orderEventProducer;

    // get inventory by product id
    public Inventory getInventory(Long productId) {
        return inventoryRepository.findByProductId(productId).orElse(null);
    }

    // update inventory quantity
    @Transactional
    public Map<String, String> updateQuantity(Long productId, Integer quantity) {
        Map<String, String> response = new HashMap<>();

        Inventory inventory = inventoryRepository
                .findByProductId(productId).orElse(null);

        if (inventory == null) {
            response.put("error", "Inventory not found");
            return response;
        }

        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);

        // send kafka event
        orderEventProducer.sendInventoryUpdateEvent(productId, quantity);

        response.put("message", "Inventory updated successfully");
        response.put("quantity", String.valueOf(quantity));
        return response;
    }

    // check if product is in stock
    public boolean isInStock(Long productId, Integer requiredQuantity) {
        Inventory inventory = inventoryRepository
                .findByProductId(productId).orElse(null);

        if (inventory == null) return false;
        return inventory.getQuantity() >= requiredQuantity;
    }

    // reduce stock when order placed
    @Transactional
    public boolean reduceStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository
                .findByProductId(productId).orElse(null);

        if (inventory == null) return false;
        if (inventory.getQuantity() < quantity) return false;

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
        return true;
    }
}