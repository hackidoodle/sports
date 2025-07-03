package com.sporty_shoe.config;

import com.sporty_shoe.bean.Admin;
import com.sporty_shoe.bean.Category;
import com.sporty_shoe.service.AdminService;
import com.sporty_shoe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin if not exists
        if (adminService.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@sportyshoes.com");
            adminService.save(admin);
            System.out.println("Default admin created: username=admin, password=admin123");
        }

        // Create default categories if they don't exist
        createCategoryIfNotExists("Running Shoes", "High-performance running shoes for all terrains");
        createCategoryIfNotExists("Basketball Shoes", "Professional basketball shoes for court performance");
        createCategoryIfNotExists("Football Boots", "Football boots for grass and artificial surfaces");
        createCategoryIfNotExists("Tennis Shoes", "Tennis shoes for hard and clay courts");
        createCategoryIfNotExists("Casual Sneakers", "Everyday casual sneakers for comfort and style");
    }

    private void createCategoryIfNotExists(String name, String description) {
        if (categoryService.findByName(name).isEmpty()) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryService.save(category);
            System.out.println("Created category: " + name);
        }
    }
}
