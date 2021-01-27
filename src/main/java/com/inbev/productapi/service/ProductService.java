package com.inbev.productapi.service;

import com.inbev.productapi.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Product save(Product any);
    Optional<Product> getById (Long id);

    void delete(Product product);

    Product update(Product product);

    Page<Product> find(Product filter, Pageable pageRequest);

    Optional<Product> getByName(String name);
}
