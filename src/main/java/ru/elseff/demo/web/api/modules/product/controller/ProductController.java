package ru.elseff.demo.web.api.modules.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.elseff.demo.persistense.Product;
import ru.elseff.demo.web.api.modules.product.dto.ProductDto;
import ru.elseff.demo.web.api.modules.product.dto.ProductUpdateDto;
import ru.elseff.demo.web.api.modules.product.service.ProductService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getAll() {
        return productService.getAll()
                .stream()
                .map(this::convertProductToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getSpecific(@PathVariable Long id) {
        return convertProductToDto(productService.getSpecific(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto addProduct(@RequestBody @Valid ProductDto productDto) {
        return convertProductToDto(productService.addProduct(productDto));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody @Valid ProductUpdateDto productUpdateDto) {
        return convertProductToDto(productService.updateProduct(id, productUpdateDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    private ProductDto convertProductToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setCountInStock(product.getCountInStock());
        productDto.setPrice(product.getPrice().getValue());
        productDto.setBarcode(product.getBarcode());
        productDto.setName(product.getName());
        productDto.setId(product.getId());
        return productDto;
    }
}
