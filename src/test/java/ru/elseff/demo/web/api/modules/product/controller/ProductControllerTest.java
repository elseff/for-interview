package ru.elseff.demo.web.api.modules.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.elseff.demo.exception.handling.dto.Violation;
import ru.elseff.demo.persistense.Price;
import ru.elseff.demo.persistense.Product;
import ru.elseff.demo.persistense.dao.ProductRepository;
import ru.elseff.demo.web.api.modules.product.dto.ProductDto;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final String endPoint = "/api/v1/products";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Context loads")
    public void contextLoads() {
        Assertions.assertNotNull(productRepository);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Get all products")
    void getAllProducts() throws Exception {
        productRepository.save(getProduct());
        productRepository.save(getProduct());

        MockHttpServletRequestBuilder request = get(endPoint)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        String response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Product> products = objectMapper.readValue(response, new TypeReference<>() {
        });

        int expectedListSize = 2;
        int actualListSize = products.size();

        Assertions.assertEquals(expectedListSize, actualListSize);
    }

    @Test
    @DisplayName("Get specific product")
    void getSpecific() throws Exception {
        Product product = productRepository.save(getProduct());
        String endPoint = this.endPoint + "/" + product.getId();

        MockHttpServletRequestBuilder request = get(endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        String response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        ProductDto productResponse = objectMapper.readValue(response, ProductDto.class);

        String expectedProductName = "Test";
        String actualProductName = productResponse.getName();

        Assertions.assertEquals(expectedProductName, actualProductName);
    }

    @Test
    @DisplayName("Get specific product if not exists")
    void getSpecific_If_Not_Exists() throws Exception {
        String endPoint = this.endPoint + "/" + 0;

        MockHttpServletRequestBuilder request = get(endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add product")
    void addProduct() throws Exception {
        ProductDto contentProduct = getProductDto();

        String productAsString = objectMapper.writeValueAsString(contentProduct);

        MockHttpServletRequestBuilder request = post(endPoint)
                .content(productAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        String response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        ProductDto responseProduct = objectMapper.readValue(response, ProductDto.class);

        String expectedProductName = "Test";
        String actualProductName = responseProduct.getName();
        Long expectedProductPrice = 10L;
        Long actualProductPrice = responseProduct.getPrice();

        Assertions.assertEquals(expectedProductName, actualProductName);
        Assertions.assertEquals(expectedProductPrice, actualProductPrice);
    }

    @Test
    @DisplayName("Add product if it is invalid")
    void addProduct_If_It_Is_Invalid() throws Exception {
        ProductDto contentProduct = getInvalidProductDto();

        String productAsString = objectMapper.writeValueAsString(contentProduct);

        MockHttpServletRequestBuilder request = post(endPoint)
                .content(productAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        String response = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<Violation> expectedListViolations = new ArrayList<>(){{
            add(new Violation("name","size must be between 3 and 255"));
        }};

        //remove first 14 characters to get a list from string
        String stringList = response.substring(14);
        List<Violation> actualStringViolations = objectMapper.readValue(stringList, new TypeReference<>() {
        });

        Assertions.assertArrayEquals(expectedListViolations.toArray(), actualStringViolations.toArray());
    }

    @Test
    @DisplayName("Update product")
    void updateProduct() throws Exception {
        Product productFromDb = productRepository.save(getProduct());
        String endPoint = this.endPoint + "/" + productFromDb.getId();

        ProductDto contentProduct = getProductDto();
        contentProduct.setName("Updated");
        contentProduct.setPrice(66L);

        String productAsString = objectMapper.writeValueAsString(contentProduct);

        MockHttpServletRequestBuilder request = patch(endPoint)
                .content(productAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        String response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductDto productDto = objectMapper.readValue(response, ProductDto.class);

        String expectedUpdatedProductName = "Updated";
        String actualUpdatedProductName = productDto.getName();
        Long expectedUpdatedProductPrice = 66L;
        Long actualUpdatedProductPrice = productDto.getPrice();

        Assertions.assertEquals(expectedUpdatedProductName,actualUpdatedProductName);
        Assertions.assertEquals(expectedUpdatedProductPrice,actualUpdatedProductPrice);
    }

    @Test
    @DisplayName("Update product if not exists")
    void updateProduct_If_Not_Exists() throws Exception {
        String endPoint = this.endPoint + "/" + 0;

        ProductDto contentProduct = getProductDto();
        contentProduct.setName("Updated");
        contentProduct.setPrice(66L);

        String productAsString = objectMapper.writeValueAsString(contentProduct);

        MockHttpServletRequestBuilder request = patch(endPoint)
                .content(productAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update product if it is invalid")
    void updateProduct_If_It_Is_Invalid() throws Exception {
        Product productFromDb = productRepository.save(getProduct());
        String endPoint = this.endPoint + "/" + productFromDb.getId();

        ProductDto contentProduct = getInvalidProductDto();

        String productAsString = objectMapper.writeValueAsString(contentProduct);

        MockHttpServletRequestBuilder request = patch(endPoint)
                .content(productAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        String response = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<Violation> expectedListViolations = new ArrayList<>(){{
            add(new Violation("name","size must be between 3 and 255"));
        }};

        //remove first 14 characters to get a list from string
        String stringList = response.substring(14);
        List<Violation> actualStringViolations = objectMapper.readValue(stringList, new TypeReference<>() {
        });

        Assertions.assertArrayEquals(expectedListViolations.toArray(), actualStringViolations.toArray());
    }

    @Test
    @DisplayName("Delete product")
    void deleteProduct() throws Exception {
        Product productFromDb = productRepository.save(getProduct());
        String endPoint = this.endPoint + "/" + productFromDb.getId();

        mockMvc.perform(delete(endPoint)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete product if not exists")
    void deleteProduct_If_Not_Exists() throws Exception {
        String endPoint = this.endPoint + "/" + 0;

        mockMvc.perform(delete(endPoint))
                .andExpect(status().isNotFound());
    }

    private ProductDto getProductDto() {
        ProductDto product = new ProductDto();
        product.setName("Test");
        product.setBarcode(1L);
        product.setPrice(10L);
        product.setCountInStock(1L);
        return product;
    }

    private ProductDto getInvalidProductDto() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Tt");
        productDto.setCountInStock(5L);
        productDto.setBarcode(5L);
        productDto.setPrice(5L);
        return productDto;
    }

    private Product getProduct() {
        Product product = new Product();
        product.setId(5L);
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