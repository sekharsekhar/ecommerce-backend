package com.sekhar.ecommerce.service;

import com.sekhar.ecommerce.model.Cart;
import com.sekhar.ecommerce.model.Role;
import com.sekhar.ecommerce.model.User;
import com.sekhar.ecommerce.repository.CartRepository;
import com.sekhar.ecommerce.repository.UserRepository;
import com.sekhar.ecommerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CartRepository cartRepository;

    public Map<String, String> register(String username, String email,
                                        String password, String role) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(username)) {
            response.put("error", "Username already exists");
            return response;
        }

        if (userRepository.existsByEmail(email)) {
            response.put("error", "Email already exists");
            return response;
        }

        // create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role.toUpperCase()));
        user.setActive(true);
        userRepository.save(user);

        // create cart for customer
        if (user.getRole() == Role.CUSTOMER) {
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        response.put("message", "User registered successfully");
        return response;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("error", "User not found");
            return response;
        }

        if (!user.isActive()) {
            response.put("error", "User account is disabled");
            return response;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("error", "Invalid password");
            return response;
        }

        String token = jwtUtil.generateToken(username, user.getRole().name());

        response.put("token", token);
        response.put("username", username);
        response.put("role", user.getRole().name());
        response.put("message", "Login successful");
        return response;
    }
}