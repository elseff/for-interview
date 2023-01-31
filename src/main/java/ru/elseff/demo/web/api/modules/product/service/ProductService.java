package ru.elseff.demo.web.api.modules.product.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.elseff.demo.persistense.Price;
import ru.elseff.demo.persistense.Product;
import ru.elseff.demo.persistense.dao.PriceRepository;
import ru.elseff.demo.persistense.dao.ProductRepository;
import ru.elseff.demo.web.api.modules.product.dto.ProductDto;
import ru.elseff.demo.web.api.modules.product.dto.ProductUpdateDto;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getSpecific(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find product with id " + id));
    }

    public Product addProduct(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        Price price = new Price(productDto.getPrice());
        price.setProduct(product);
        product.setPrice(price);
        return productRepository.save(product);
    }


    public Product updateProduct(Long id, ProductUpdateDto productUpdateDto) {
        Product productFromDb = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find product with id " + id));

        if (productUpdateDto.getName() != null) {
            productFromDb.setName(productUpdateDto.getName());
        }
        if (productUpdateDto.getPrice() != null) {
            Price priceFromDb = priceRepository.findByProductId(productFromDb.getId());
            priceFromDb.setPrice(productUpdateDto.getPrice());
            productFromDb.setPrice(priceRepository.save(priceFromDb));
        }
        if (productUpdateDto.getBarcode() != null) {
            productFromDb.setBarcode(productUpdateDto.getBarcode());
        }
        if (productUpdateDto.getCountInStock() != null) {
            productFromDb.setCountInStock(productUpdateDto.getCountInStock());
        }
        return productRepository.save(productFromDb);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find product with id " + id));
        productRepository.delete(product);
    }
}
