package com.sporty_shoe.repository;

import com.sporty_shoe.bean.Purchase;
import com.sporty_shoe.bean.User;
import com.sporty_shoe.bean.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUser(User user);

    List<Purchase> findByUserId(Long userId);

    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    List<Purchase> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Purchase p WHERE p.product.category = :category")
    List<Purchase> findByCategory(@Param("category") Category category);

    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate AND p.product.category = :category")
    List<Purchase> findByDateRangeAndCategory(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("category") Category category);
}
