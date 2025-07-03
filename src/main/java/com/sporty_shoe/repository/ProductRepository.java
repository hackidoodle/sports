package com.sporty_shoe.repository;

import com.sporty_shoe.bean.Product;
import com.sporty_shoe.bean.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);

    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.brand LIKE %:keyword%")
    List<Product> searchProducts(@Param("keyword") String keyword);

    List<Product> findByStockQuantityGreaterThan(Integer quantity);
}
