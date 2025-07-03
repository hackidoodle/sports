package com.sporty_shoe.service;

import com.sporty_shoe.bean.Product;
import com.sporty_shoe.bean.Category;
import com.sporty_shoe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public List<Product> findAvailableProducts() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(product.getStockQuantity() - quantity);
            return productRepository.save(product);
        }
        return null;
    }

    public boolean isProductAvailable(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return product.getStockQuantity() >= quantity;
        }
        return false;
    }

    public boolean canPurchase(Long productId, Integer quantity) {
        return isProductAvailable(productId, quantity);
    }

    // Additional customer-focused methods
    public List<Product> getRecommendedProducts(int limit) {
        List<Product> availableProducts = findAvailableProducts();
        return availableProducts.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Product> getPopularProducts(int limit) {
        // For now, return available products with high stock
        return productRepository.findByStockQuantityGreaterThan(10)
                .stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean hasStock(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.isPresent() && productOpt.get().getStockQuantity() > 0;
    }

    public int getAvailableStock(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(Product::getStockQuantity).orElse(0);
    }
}
