package com.inbev.productapi.api.resource;

import com.inbev.productapi.exception.BusinessException;
import com.inbev.productapi.model.entity.Product;
import com.inbev.productapi.service.ProductService;
import com.inbev.productapi.api.dto.ProductDTO;
import com.inbev.productapi.api.excptions.ApiErrors;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@Api("Product API")
public class ProductController {

    private ProductService service;
    private ModelMapper modelMapper;

    public ProductController(ProductService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a product")
    public ProductDTO create(@RequestBody  @Valid ProductDTO dto){
        Product entity = modelMapper.map(dto, Product.class);
        entity = service.save(entity);
        return modelMapper.map(entity, ProductDTO.class);
    }
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("get a product details by id")
    public ProductDTO get (@PathVariable Long id){
        return service.getById(id)
                .map( product -> modelMapper.map(product, ProductDTO.class))
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND));
    }
    @GetMapping("/findByName/{name}")
    @ApiOperation("find product details by name")
    public ProductDTO findByName(@PathVariable String name) {
        return service.getByName(name)
                .map( product -> modelMapper.map(product, ProductDTO.class))
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("delete a product")
    public void delete(@PathVariable Long id){
        Product product = service.getById(id)
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND));;
        service.delete(product);
    }
    @PutMapping("{id}")
    @ApiOperation("update a product")
    public ProductDTO update (@PathVariable Long id, @RequestBody ProductDTO dto ){
        return service.getById(id).map( product -> {
            product.setName(dto.getName());
            product = service.update(product);
            return modelMapper.map(product, ProductDTO.class);
        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handlerBusinessException( BusinessException ex){
        return new ApiErrors(ex);
    }

}
