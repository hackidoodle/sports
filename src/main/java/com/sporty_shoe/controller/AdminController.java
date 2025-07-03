package com.sporty_shoe.controller;

import com.sporty_shoe.bean.Admin;
import com.sporty_shoe.bean.User;
import com.sporty_shoe.bean.Product;
import com.sporty_shoe.bean.Category;
import com.sporty_shoe.bean.Purchase;
import com.sporty_shoe.service.AdminService;
import com.sporty_shoe.service.UserService;
import com.sporty_shoe.service.ProductService;
import com.sporty_shoe.service.CategoryService;
import com.sporty_shoe.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String adminLoginPost(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<Admin> adminOpt = adminService.findByUsername(username);

        if (adminOpt.isPresent() && adminService.validatePassword(password, adminOpt.get().getPassword())) {
            session.setAttribute("admin", adminOpt.get());
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("totalProducts", productService.findAll().size());
        model.addAttribute("totalCategories", categoryService.findAll().size());
        model.addAttribute("totalPurchases", purchaseService.findAll().size());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model, @RequestParam(required = false) String search) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search);
        } else {
            users = userService.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("search", search);

        return "admin/users";
    }

    @GetMapping("/products")
    public String products(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProduct(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/add-product";
    }

    @PostMapping("/products/add")
    public String addProductPost(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully");
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String categories(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", new Category());

        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Category added successfully");
        return "redirect:/admin/categories";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        List<Purchase> purchases;

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");

            if (categoryId != null && categoryId > 0) {
                Optional<Category> categoryOpt = categoryService.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    purchases = purchaseService.findByDateRangeAndCategory(start, end, categoryOpt.get());
                } else {
                    purchases = purchaseService.findByDateRange(start, end);
                }
            } else {
                purchases = purchaseService.findByDateRange(start, end);
            }
        } else {
            purchases = purchaseService.findAll();
        }

        model.addAttribute("purchases", purchases);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/reports";
    }

    @GetMapping("/change-password")
    public String changePassword(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordPost(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Admin admin = (Admin) session.getAttribute("admin");

        if (admin == null) {
            return "redirect:/admin/login";
        }

        if (!adminService.validatePassword(currentPassword, admin.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/admin/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/admin/change-password";
        }

        adminService.updatePassword(admin, newPassword);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully");

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
