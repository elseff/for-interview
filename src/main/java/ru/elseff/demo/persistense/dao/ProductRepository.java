package ru.elseff.demo.persistense.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.elseff.demo.persistense.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
