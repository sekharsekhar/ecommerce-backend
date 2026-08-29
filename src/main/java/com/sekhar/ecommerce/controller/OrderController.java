package com.sekhar.ecommerce.controller;

import com.sekhar.ecommerce.model.Order;
import com.sekhar.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // place order
    @PostMapping("/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> placeOrder(
            Authentication authentication) {

        Map<String, Object> response = orderService
                .placeOrder(authentication.getName());

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // get my orders
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Order>> getMyOrders(
            Authentication authentication) {
        return ResponseEntity.ok(
                orderService.getOrdersByUsername(authentication.getName()));
    }

    // cancel order
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        Map<String, String> response = orderService
                .cancelOrder(id, authentication.getName());

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // update order status - admin/seller
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Map<String, String> response = orderService
                .updateOrderStatus(id, request.get("status"));

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}