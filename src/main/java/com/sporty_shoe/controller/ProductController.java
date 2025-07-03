package com.sporty_shoe.controller;

import com.sporty_shoe.bean.Product;
import com.sporty_shoe.service.ProductService;
import com.sporty_shoe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String products(Model model, @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        List<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
        } else {
            products = productService.findAvailableProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("search", search);

        return "products";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productService.findById(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "product-detail";
        } else {
            return "redirect:/products";
        }
    }
}
