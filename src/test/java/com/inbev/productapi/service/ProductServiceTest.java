package com.inbev.productapi.service;

import com.inbev.productapi.exception.BusinessException;
import com.inbev.productapi.model.entity.Product;
import com.inbev.productapi.model.repository.ProductRepository;
import com.inbev.productapi.service.impl.ProductServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProductServiceTest {

    ProductService service;

    @MockBean
    ProductRepository respository;

    @BeforeEach
    public void setUp(){
        this.service = new ProductServiceImp(respository);
    }

    @Test
    @DisplayName("Must save a Product")
    public void saveProductTest(){
        //scenario
        Product product = createValidProduct();
        Mockito.when(respository.existsByName(Mockito.anyString())).thenReturn(false);
        Mockito.when(respository.save(product) ).thenReturn(
                    Product.builder().id(11L)
                            .brand("123")
                            .description("fulano")
                            .name("As aventuras")
                            .price(100.0)
                            .build()
                );

        //execution
        Product savedProduct = service.save(product);

        //verification
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getDescription()).isEqualTo("fulano");
        assertThat(savedProduct.getName()).isEqualTo("As aventuras");
        assertThat(savedProduct.getBrand()).isEqualTo("123");
        assertThat(savedProduct.getPrice()).isEqualTo(100);

    }

    @Test
    @DisplayName("Must a show BusinessErrors when try save a product whit duplicate Name")
    public void shouldNotSaveAProductWhitDublicateName() {
        //given
        long id = 1;
        Product product = createValidProduct();
        product.setId(id);
        Mockito.when(respository.existsByName(Mockito.anyString())).thenReturn(true);

        //then
       Throwable execption = Assertions.catchThrowable( () -> service.save(product));

       //when
       assertThat(execption)
               .isInstanceOf(BusinessException.class)
               .hasMessage("Name already registered");

       Mockito.verify(respository,Mockito.never()).save(product);
    }
    @Test
    @DisplayName("Must get a product by id")
    public void getByIdTest(){
        //given
        Long id = 11L;
        Product product = createValidProduct();
        product.setId(id);

        //when
        Mockito.when(respository.findById(id)).thenReturn(Optional.of(product));
        Optional<Product> foundProduct = service.getById(id);

        //then
       assertThat(foundProduct.isPresent()).isTrue();
       assertThat(foundProduct.get().getId()).isEqualTo(product.getId());
       assertThat(foundProduct.get().getName()).isEqualTo(product.getName());
       assertThat(foundProduct.get().getDescription()).isEqualTo(product.getDescription());
       assertThat(foundProduct.get().getBrand()).isEqualTo(product.getBrand());
        assertThat(foundProduct.get().getPrice()).isEqualTo(product.getPrice());
    }
    @Test
    @DisplayName("Must return empty when trying to find a product by nonexistent id")
    public void productNotFoundByIdTest(){
        //given
        Long id = 11L;

        //when
        Mockito.when(respository.findById(id)).thenReturn(Optional.empty());
        Optional<Product> product = service.getById(id);

        //then
        assertThat(product.isPresent()).isFalse();

    }
    @Test
    @DisplayName("Must get a product by name")
    public void getByNameTest(){
        //given
        String name = "corona";
        Product product = createValidProduct();
        product.setName(name);

        //when
        Mockito.when(respository.findByName(name)).thenReturn(Optional.of(product));
        Optional<Product> foundProduct = service.getByName(name);

        //then
        assertThat(foundProduct.isPresent()).isTrue();
        assertThat(foundProduct.get().getId()).isEqualTo(product.getId());
        assertThat(foundProduct.get().getName()).isEqualTo(product.getName());
        assertThat(foundProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(foundProduct.get().getBrand()).isEqualTo(product.getBrand());
        assertThat(foundProduct.get().getPrice()).isEqualTo(product.getPrice());
    }
    @Test
    @DisplayName("Must return empty when trying to find a product by nonexistent name")
    public void productNotFoundByNameTest(){
        //given
        String name = "corona";

        //when
        Mockito.when(respository.findByName(name)).thenReturn(Optional.empty());
        Optional<Product> product = service.getByName(name);

        //then
        assertThat(product.isPresent()).isFalse();

    }
    @Test
    @DisplayName("Must delete a product when finding by Id")
    public void deleteProductTest(){
        //given
        Product product = Product.builder().id(11L).build();

        //when
         org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(product));

        //then
        Mockito.verify(respository,Mockito.times(1)).delete(product);
    }
    @Test
    @DisplayName("Must return a error when try delete a product with not valid id")
    public void deleteProductWithNotValidIdTest(){
        //given
        Long id = 11L;
        Product product = new Product();
        Mockito.when(respository.findById(id)).thenReturn(Optional.of(product));

        //when
        Throwable execption = Assertions.catchThrowable( () -> service.delete(product));

        //then

        assertThat(execption)
                .isInstanceOf((IllegalArgumentException.class))
                .hasMessage("Product id cant be null");
        Mockito.verify(respository,Mockito.never()).delete(product);
    }
    @Test
    @DisplayName("Must update a product")
    public void updateProductTest(){
        //given
        long id = 11;
        Product updateProduct = createValidProduct();
        updateProduct.setId(id);

        //when
        Mockito.when(respository.save(updateProduct)).thenReturn(updateProduct);

        Product product = org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.update(updateProduct));

        //then
        Mockito.verify(respository,Mockito.times(1)).save(updateProduct);
        assertThat(product.getId()).isEqualTo(updateProduct.getId());
        assertThat(product.getName()).isEqualTo(updateProduct.getName());
        assertThat(product.getDescription()).isEqualTo(updateProduct.getDescription());
        assertThat(product.getBrand()).isEqualTo(updateProduct.getBrand());
        assertThat(product.getPrice()).isEqualTo(updateProduct.getPrice());

    }
    @Test
    @DisplayName("Must a show a error when try save a product whitout Id")
    public void shouldNotUpdateAProductWhitOutIdProduct()
    {
        //given
        Product product = new Product();

        //when
        Throwable execption = Assertions.catchThrowable( () -> service.update(product));

        //then
        assertThat(execption)
                .isInstanceOf((IllegalArgumentException.class))
                .hasMessage("Product id cant be null");

        Mockito.verify(respository,Mockito.never()).save(product);
    }
    private Product createValidProduct() {
        return Product.builder().brand("123").name("fulano").description("As aventuras").price(100.0).build();
    }
}

