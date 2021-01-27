package com.inbev.productapi.model.respository;

import com.inbev.productapi.model.entity.Product;
import com.inbev.productapi.model.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static  org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ProductRepository repository;

    @Test
    @DisplayName("Must return false when there is a product with the informed name in the database")
    public void returnTrueWheNameExists(){
        //scenario
        String name = "corona";
        Product product = createNewProduct();
        entityManager.persist(product);

        //execution
        boolean exists = repository.existsByName(name);

        //verification
        assertThat(exists).isTrue();
    }
    @Test
    @DisplayName("must return false when there is no product with the informed name in the database")
    public void returnFalseWheNameDoesntExists(){
        //scenario
        String name = "corona";

        //execution
        boolean exists = repository.existsByName(name);

        //verification
        assertThat(exists).isFalse();
    }
    @Test
    @DisplayName("Must get a product by id")
    public void findByIdTest(){
        //given
        Product product = createNewProduct();
        entityManager.persist(product);
        //when
        Optional<Product> foundProduct = repository.findById(product.getId());

        //then
        assertThat(foundProduct.isPresent()).isTrue();
    }
    @Test
    @DisplayName("Must get a product by name")
    public void findByNameTest(){
        //given
        Product product = createNewProduct();
        entityManager.persist(product);
        //when
        Optional<Product> foundProduct = repository.findByName(product.getName());

        //then
        assertThat(foundProduct.isPresent()).isTrue();
    }
    @Test
    @DisplayName("Must save a product")
    public void saveProductTest(){
        //given
        Product product = createNewProduct();

        //when
        Product saveProduct = repository.save(product);

        //then
        assertThat(saveProduct.getId()).isNotNull();

    }

    @Test
    @DisplayName("Must delete a product")
    public void deleteProductTest(){
        //given
        Product product = createNewProduct();
        entityManager.persist(product);
        Product foundProduct =  entityManager.find(Product.class, product.getId());

        //when
        repository.delete(foundProduct);

        //then
        Product deletedProduct = entityManager.find(Product.class, product.getId());
        assertThat(deletedProduct).isNull();

    }
    private Product createNewProduct() {

        return Product.builder().name("corona").description("As aventuras").brand("123").price(100.0).build();
    }
}
