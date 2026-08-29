package com.sekhar.ecommerce.repository;

import com.sekhar.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByActiveTrue();
    List<Product> findBySellerId(Long sellerId);
}