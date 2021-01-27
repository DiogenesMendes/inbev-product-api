package com.inbev.productapi.service.impl;

import com.inbev.productapi.service.ProductService;
import com.inbev.productapi.exception.BusinessException;
import com.inbev.productapi.model.entity.Product;
import com.inbev.productapi.model.repository.ProductRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImp implements ProductService {

    private ProductRepository repository;

    public ProductServiceImp(ProductRepository repository) {

        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        if(repository.existsByName(product.getName())){
            throw new BusinessException("Name already registered");
        }
        return repository.save(product);
    }

    @Override
    public Optional<Product> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Product product) {
        if (product == null || product.getId() == null){
            throw new IllegalArgumentException("Product id cant be null");
        }
        this.repository.delete(product);
    }

    @Override
    public Product update(Product product) {
        if (product == null || product.getId() == null){
            throw new IllegalArgumentException("Product id cant be null");
        }
       return this.repository.save(product);
    }

    @Override
    public Page<Product> find(Product filter, Pageable pageRequest) {
        Example<Product> example = Example.of(filter,
                ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Product> getByName(String name) {
        return this.repository.findByName(name);
    }
}
