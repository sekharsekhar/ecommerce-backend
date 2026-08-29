package com.sekhar.ecommerce.service;

import com.sekhar.ecommerce.model.*;
import com.sekhar.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // get cart by username
    public Cart getCart(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        return cartRepository.findByUserId(user.getId()).orElse(null);
    }

    // add item to cart
    public Map<String, String> addToCart(String username,
                                         Long productId, Integer quantity) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("error", "User not found");
            return response;
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            response.put("error", "Product not found");
            return response;
        }

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart == null) {
            response.put("error", "Cart not found");
            return response;
        }

        // check if product already in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        response.put("message", "Item added to cart successfully");
        return response;
    }

    // remove item from cart
    public Map<String, String> removeFromCart(String username, Long productId) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart == null) {
            response.put("error", "Cart not found");
            return response;
        }

        cart.getItems().removeIf(item ->
                item.getProduct().getId().equals(productId));
        cartRepository.save(cart);

        response.put("message", "Item removed from cart");
        return response;
    }
}