package ru.elseff.demo.web.api.modules.product.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;
import ru.elseff.demo.persistense.Price;
import ru.elseff.demo.persistense.Product;
import ru.elseff.demo.persistense.dao.ProductRepository;
import ru.elseff.demo.web.api.modules.product.dto.ProductDto;
import ru.elseff.demo.web.api.modules.product.dto.ProductUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get all products")
    void getAll() {
        given(productRepository.findAll()).willReturn(List.of(
                new Product(),
                new Product(),
                new Product()
        ));

        List<Product> products = productService.getAll();

        int expectedListSize = 3;
        int actualListSize = products.size();

        Assertions.assertEquals(expectedListSize, actualListSize);
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Get specific product")
    void getSpecific() {
        given(productRepository.findById(anyLong())).willReturn(java.util.Optional.of(getProduct()));

        Product product = productService.getSpecific(anyLong());

        String expectedProductName = "Test";
        String actualProductName = product.getName();

        Assertions.assertNotNull(product);
        Assertions.assertEquals(expectedProductName, actualProductName);

        verify(productRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Get specific product is not exists")
    void getSpecific_If_Not_Exists() {
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> productService.getSpecific(1L));

        String expectedExceptionMessage = "Could not find product with id 1";
        String actualExceptionMessage = exception.getReason();

        Assertions.assertEquals(expectedExceptionMessage, actualExceptionMessage);

        verify(productRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Add product")
    void addProduct() {
        given(productRepository.save(any(Product.class))).willReturn(getProduct());
        given(modelMapper.map(any(), any())).willReturn(getProduct());

        Product product = productService.addProduct(new ProductDto());

        String expectedProductName = "Test";
        String actualProductName = product.getName();

        Assertions.assertNotNull(product);
        Assertions.assertEquals(expectedProductName, actualProductName);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(modelMapper, times(1)).map(any(), any());
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    @DisplayName("Update product")
    void updateProduct() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(getProduct()));
        Product savedProduct = getProduct();
        savedProduct.setName("Updated");
        savedProduct.getPrice().setValue(99L);
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        Product product = productService.updateProduct(anyLong(), new ProductUpdateDto());

        String expectedProductName = "Updated";
        String actualProductName = product.getName();
        Long expectedProductPrice = 99L;
        Long actualProductPrice = product.getPrice().getValue();

        Assertions.assertNotNull(product);
        Assertions.assertEquals(expectedProductName, actualProductName);
        Assertions.assertEquals(expectedProductPrice,actualProductPrice);

        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Update product if not exists")
    void updateProduct_If_Not_Exists() {
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> productService.updateProduct(1L, new ProductUpdateDto()));

        String expectedExceptionMessage = "Could not find product with id 1";
        String actualExceptionMessage = exception.getReason();

        Assertions.assertEquals(expectedExceptionMessage, actualExceptionMessage);

        verify(productRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Delete product")
    void deleteProduct() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(getProduct()));
        willDoNothing().given(productRepository).delete(any(Product.class));

        productService.deleteProduct(anyLong());

        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).delete(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Delete product is not exists")
    void deleteProduct_If_Not_Exists() {
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> productService.deleteProduct(1L));

        String expectedExceptionMessage = "Could not find product with id 1";
        String actualExceptionMessage = exception.getReason();

        Assertions.assertEquals(expectedExceptionMessage, actualExceptionMessage);

        verify(productRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(productRepository);
    }

    private Product getProduct() {
        Product product = new Product();
        product.setName("Test");
        product.setBarcode(1L);
        Price price = new Price();
        price.setValue(1L);
        price.setProduct(product);
        product.setPrice(price);
        product.setCountInStock(1L);
        return product;
    }
}