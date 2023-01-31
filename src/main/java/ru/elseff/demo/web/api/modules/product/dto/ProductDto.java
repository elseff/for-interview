package ru.elseff.demo.web.api.modules.product.dto;

import lombok.*;

import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductDto {

    @Null
    private Long id;

    @NotNull
    @Size(min = 3, max = 255)
    @Pattern(regexp = "([A-Z][a-zA-Z]*)", message = "name should be valid")
    private String name;

    @NotNull
    @Positive
    private Long barcode;

    @NotNull
    @Positive
    private Long countInStock;

    @NotNull
    @Positive
    private Long price;
}
