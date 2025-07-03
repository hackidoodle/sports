package com.sporty_shoe.service;

import com.sporty_shoe.bean.Purchase;
import com.sporty_shoe.bean.User;
import com.sporty_shoe.bean.Category;
import com.sporty_shoe.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public Optional<Purchase> findById(Long id) {
        return purchaseRepository.findById(id);
    }

    public List<Purchase> findByUser(User user) {
        return purchaseRepository.findByUser(user);
    }

    public List<Purchase> findByUserId(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }

    public List<Purchase> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseRepository.findByDateRange(startDate, endDate);
    }

    public List<Purchase> findByCategory(Category category) {
        return purchaseRepository.findByCategory(category);
    }

    public List<Purchase> findByDateRangeAndCategory(LocalDateTime startDate, LocalDateTime endDate,
            Category category) {
        return purchaseRepository.findByDateRangeAndCategory(startDate, endDate, category);
    }
}
