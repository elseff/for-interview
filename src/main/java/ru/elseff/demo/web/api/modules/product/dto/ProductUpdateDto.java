package ru.elseff.demo.web.api.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {

    @Size(min = 3, max = 255)
    @Pattern(regexp = "([A-Z][a-zA-Z]*)", message = "name should be valid")
    private String name;

    @Positive
    private Long barcode;

    @Positive
    private Long countInStock;

    @Positive
    private Long price;
}
