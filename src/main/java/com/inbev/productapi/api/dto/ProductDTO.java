package com.inbev.productapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private double price;

    @NotEmpty
    private String brand;
}
