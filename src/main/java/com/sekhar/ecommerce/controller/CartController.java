package com.sekhar.ecommerce.controller;

import com.sekhar.ecommerce.model.Cart;
import com.sekhar.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    @Autowired
    private CartService cartService;

    // get cart
    @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
        Cart cart = cartService.getCart(authentication.getName());
        if (cart == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cart not found"));
        }
        return ResponseEntity.ok(cart);
    }

    // add to cart
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = (Integer) request.get("quantity");

        Map<String, String> response = cartService.addToCart(
                authentication.getName(), productId, quantity);

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // remove from cart
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, String>> removeFromCart(
            @PathVariable Long productId,
            Authentication authentication) {

        Map<String, String> response = cartService.removeFromCart(
                authentication.getName(), productId);

        return ResponseEntity.ok(response);
    }
}