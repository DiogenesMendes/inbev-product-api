package com.inbev.productapi.model.repository;

import com.inbev.productapi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}
