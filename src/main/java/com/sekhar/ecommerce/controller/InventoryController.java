package com.sekhar.ecommerce.controller;

import com.sekhar.ecommerce.model.Inventory;
import com.sekhar.ecommerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // get inventory by product id
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<?> getInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventory(productId);
        if (inventory == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Inventory not found"));
        }
        return ResponseEntity.ok(inventory);
    }

    // update inventory
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Map<String, String>> updateInventory(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {

        Integer quantity = request.get("quantity");
        Map<String, String> response = inventoryService
                .updateQuantity(productId, quantity);

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}