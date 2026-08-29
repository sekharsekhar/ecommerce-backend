package com.sekhar.ecommerce.service;

import com.sekhar.ecommerce.model.*;
import com.sekhar.ecommerce.repository.*;
import com.sekhar.ecommerce.kafka.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderEventProducer orderEventProducer;

    // place order from cart
    @Transactional
    public Map<String, Object> placeOrder(String username) {
        Map<String, Object> response = new HashMap<>();

        // get user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("error", "User not found");
            return response;
        }

        // get cart
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            response.put("error", "Cart is empty");
            return response;
        }

        // check inventory for all items
        for (CartItem cartItem : cart.getItems()) {
            if (!inventoryService.isInStock(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity())) {
                response.put("error", "Product out of stock: "
                        + cartItem.getProduct().getName());
                return response;
            }
        }

        // calculate total amount
        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // create order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);

            // reduce stock
            inventoryService.reduceStock(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity()
            );
        }

        order.setItems(orderItems);
        orderRepository.save(order);

        // clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        // send kafka event
        orderEventProducer.sendOrderPlacedEvent(order.getId());

        response.put("message", "Order placed successfully");
        response.put("orderId", order.getId());
        response.put("totalAmount", totalAmount);
        response.put("status", OrderStatus.PENDING);
        return response;
    }

    // get orders by username
    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ArrayList<>();
        return orderRepository.findByUserId(user.getId());
    }

    // cancel order
    @Transactional
    public Map<String, String> cancelOrder(Long orderId, String username) {
        Map<String, String> response = new HashMap<>();

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            response.put("error", "Order not found");
            return response;
        }

        if (!order.getUser().getUsername().equals(username)) {
            response.put("error", "Unauthorized to cancel this order");
            return response;
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            response.put("error", "Cannot cancel delivered order");
            return response;
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // send kafka event
        orderEventProducer.sendOrderCancelledEvent(orderId);

        response.put("message", "Order cancelled successfully");
        return response;
    }

    // update order status (admin/seller)
    public Map<String, String> updateOrderStatus(Long orderId, String status) {
        Map<String, String> response = new HashMap<>();

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            response.put("error", "Order not found");
            return response;
        }

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);

        response.put("message", "Order status updated successfully");
        response.put("status", status);
        return response;
    }
}