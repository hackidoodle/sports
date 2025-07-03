package com.sporty_shoe.controller;

import com.sporty_shoe.bean.User;
import com.sporty_shoe.bean.Product;
import com.sporty_shoe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to add items to cart");
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.findById(productId);
        if (!productOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        }

        Product product = productOpt.get();

        // Check if product is available in requested quantity
        if (!productService.canPurchase(productId, quantity)) {
            redirectAttributes.addFlashAttribute("error",
                    "Insufficient stock. Available: " + product.getStockQuantity());
            return "redirect:/products/" + productId;
        }

        // Get or create cart from session
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Add or update quantity in cart
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
        session.setAttribute("cart", cart);

        redirectAttributes.addFlashAttribute("success",
                product.getName() + " added to cart!");

        return "redirect:/products/" + productId;
    }

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        Map<Product, Integer> cartItems = new HashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Product> productOpt = productService.findById(entry.getKey());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                cartItems.put(product, entry.getValue());
                totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(entry.getValue())));
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("user", user);

        return "user/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }

        redirectAttributes.addFlashAttribute("success", "Item removed from cart");
        return "redirect:/cart/view";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("success", "Cart cleared");
        return "redirect:/cart/view";
    }
}
