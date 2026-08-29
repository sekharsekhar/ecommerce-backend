package com.sekhar.ecommerce.service;

import com.sekhar.ecommerce.model.Inventory;
import com.sekhar.ecommerce.model.Product;
import com.sekhar.ecommerce.model.User;
import com.sekhar.ecommerce.repository.InventoryRepository;
import com.sekhar.ecommerce.repository.ProductRepository;
import com.sekhar.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    // get all active products
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    // get product by id
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // add new product (seller only)
    public Map<String, Object> addProduct(String name, String description,
                                          BigDecimal price, String category, Integer quantity, String username) {
        Map<String, Object> response = new HashMap<>();

        User seller = userRepository.findByUsername(username).orElse(null);
        if (seller == null) {
            response.put("error", "Seller not found");
            return response;
        }

        // create product
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setSeller(seller);
        product.setActive(true);
        productRepository.save(product);

        // create inventory
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);

        response.put("message", "Product added successfully");
        response.put("productId", product.getId());
        return response;
    }

    // update product
    public Map<String, Object> updateProduct(Long id, String name,
                                             String description, BigDecimal price) {
        Map<String, Object> response = new HashMap<>();

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            response.put("error", "Product not found");
            return response;
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        productRepository.save(product);

        response.put("message", "Product updated successfully");
        return response;
    }

    // delete product
    public Map<String, String> deleteProduct(Long id) {
        Map<String, String> response = new HashMap<>();

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            response.put("error", "Product not found");
            return response;
        }

        product.setActive(false);
        productRepository.save(product);

        response.put("message", "Product deleted successfully");
        return response;
    }
}