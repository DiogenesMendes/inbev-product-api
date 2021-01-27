package com.inbev.productapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbev.productapi.api.dto.ProductDTO;
import com.inbev.productapi.exception.BusinessException;
import com.inbev.productapi.model.entity.Product;
import com.inbev.productapi.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    static  String PRODUCT_API = "/api/product";

    @Autowired
    MockMvc mvc;
    @MockBean
    ProductService service;

    @Test
    @DisplayName("Must successfully create a product")
    public void createProductTest() throws Exception{

        ProductDTO dto = createNewProductDTO();
        Product savedproduct = Product.builder().id(101L).name("Artur").description("test").brand("corona").price(10.15).build();

        BDDMockito.given(service.save(Mockito.any(Product.class))).willReturn(savedproduct);
        String json = new ObjectMapper().writeValueAsString(dto);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUCT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101))
                .andExpect(jsonPath("name").value(createNewProductDTO().getName()))
                .andExpect(jsonPath("description").value(createNewProductDTO().getDescription()))
                .andExpect(jsonPath("brand").value(createNewProductDTO().getBrand()))
                .andExpect(jsonPath("price").value(createNewProductDTO().getPrice()));
    }

    @Test
    @DisplayName("Must throw an error when trying to create an incomplete product")
    public void createInvalidProductTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new ProductDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUCT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Must throw an error when you have a duplicate Name")
    public void createProductWithDuplicatedName() throws Exception{

        ProductDTO dto = createNewProductDTO();
        String messageError = "name already registered";
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(service.save(Mockito.any(Product.class))).
                willThrow( new BusinessException(messageError));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUCT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));

    }
    @Test
    @DisplayName("must get information from a product")
    public void getProductDetailsTest() throws Exception{
        //given
        Long id = 11L;
        Product product = Product.builder()
                .id(id)
                .name(createNewProductDTO().getName())
                .description(createNewProductDTO().getDescription())
                .brand(createNewProductDTO().getBrand())
                .price(createNewProductDTO().getPrice())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(product));

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUCT_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewProductDTO().getName()))
                .andExpect(jsonPath("description").value(createNewProductDTO().getDescription()))
                .andExpect(jsonPath("brand").value(createNewProductDTO().getBrand()))
                .andExpect(jsonPath("price").value(createNewProductDTO().getPrice()));

    }
    @Test
    @DisplayName("must return a resource not found when the requested product does not exist")
    public void productNotFoundTest() throws Exception{
        //given
        BDDMockito.given(service.getById(1l)).willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUCT_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Must delete a product")
    public void deleteProductTest() throws Exception{
        //given
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.of(Product.builder().id(11L).build()));

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(PRODUCT_API.concat("/" + 1));
        //then
        mvc.perform(request)
                .andExpect( status().isNoContent());

    }
    @Test
    @DisplayName("Must return resource not found when not found the product to delete")
    public void deleteNonexistentProductTest() throws Exception{
        //given
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(PRODUCT_API.concat("/" + 1));
        //then
        mvc.perform(request)
                .andExpect( status().isNotFound());

    }
    @Test
    @DisplayName("Must update a product")
    public  void updateProductTest() throws Exception{
        //given
        Long id = 11L;
        String json = new ObjectMapper().writeValueAsString(createNewProductDTO());

        Product updatingproduct = Product.builder().id(11L).name("some title").description("some author").brand("321").price(100.0).build();
        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(updatingproduct));
        Product updatedproduct = Product.builder().id(id).name("Artur").price(10.15).description("test").brand("corona").build();
        BDDMockito.given(service.update(updatingproduct)).willReturn(updatedproduct);

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PRODUCT_API.concat("/" + 11L))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(request)
                .andExpect( status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewProductDTO().getName()))
                .andExpect(jsonPath("description").value(createNewProductDTO().getDescription()))
                .andExpect(jsonPath("brand").value(createNewProductDTO().getBrand()))
                .andExpect(jsonPath("price").value(createNewProductDTO().getPrice()));
    }
    @Test
    @DisplayName("Must return resourc not found when try update a non-existent product")
    public  void updateNonexistentProductTest() throws Exception{
        //given
        String json = new ObjectMapper().writeValueAsString(createNewProductDTO());
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PRODUCT_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(request)
                .andExpect( status().isNotFound());
    }
    @Test
    @DisplayName("Must filter the product")
    public void findProductTest() throws Exception{
        //given
        Long id = 11L;
        Product product = Product.builder().id(id)
                .name(createNewProductDTO().getName())
                .description(createNewProductDTO().getDescription())
                .brand(createNewProductDTO().getBrand())
                .price(createNewProductDTO().getPrice())
                .build();

        BDDMockito.given( service.find(Mockito.any(Product.class),Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Product>(Arrays.asList(product), PageRequest.of(0,100),1));

        String queryString = String.format("?name=%s&name=%s&price=0&size=100",
                product.getName(), product.getPrice());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUCT_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect( status().isOk())
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect( jsonPath("pageable.pageNumber").value(0));
    }
    private ProductDTO createNewProductDTO() {
        return ProductDTO.builder().name("Artur").price(10.15).description("test").brand("corona").build();
    }


}
