package com.sekhar.ecommerce.controller;

import com.sekhar.ecommerce.model.Product;
import com.sekhar.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // get all products - public
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // get product by id - public
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found"));
        }
        return ResponseEntity.ok(product);
    }

    // get products by category - public
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(
                productService.getProductsByCategory(category));
    }

    // add product - seller only
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String name = (String) request.get("name");
        String description = (String) request.get("description");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        Integer quantity = (Integer) request.get("quantity");
        String username = authentication.getName();

        Map<String, Object> response = productService.addProduct(
                name, description, price, category, quantity, username);

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // update product - seller only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        String name = (String) request.get("name");
        String description = (String) request.get("description");
        BigDecimal price = new BigDecimal(request.get("price").toString());

        Map<String, Object> response = productService.updateProduct(
                id, name, description, price);

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // delete product - seller only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id) {

        Map<String, String> response = productService.deleteProduct(id);

        if (response.containsKey("error")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}