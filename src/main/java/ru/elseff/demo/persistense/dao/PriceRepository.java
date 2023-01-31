package ru.elseff.demo.persistense.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.elseff.demo.persistense.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByProductId(Long id);
}
