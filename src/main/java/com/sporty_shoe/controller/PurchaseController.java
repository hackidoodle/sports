package com.sporty_shoe.controller;

import com.sporty_shoe.bean.User;
import com.sporty_shoe.bean.Product;
import com.sporty_shoe.bean.Purchase;
import com.sporty_shoe.service.ProductService;
import com.sporty_shoe.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/buy")
    public String buyProduct(@RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to purchase products");
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

        // Calculate total price
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));

        // Create purchase record
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setProduct(product);
        purchase.setQuantity(quantity);
        purchase.setTotalPrice(totalPrice);
        purchase.setStatus("CONFIRMED");

        // Save purchase
        purchaseService.save(purchase);

        // Update product stock
        productService.updateStock(productId, quantity);

        redirectAttributes.addFlashAttribute("success",
                "Purchase successful! Your order has been confirmed.");

        return "redirect:/purchase/my-orders";
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Purchase> purchases = purchaseService.findByUserId(user.getId());
        model.addAttribute("purchases", purchases);
        model.addAttribute("user", user);

        return "user/orders";
    }

    @GetMapping("/order/{id}")
    public String orderDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Purchase> purchaseOpt = purchaseService.findById(id);
        if (!purchaseOpt.isPresent()) {
            return "redirect:/purchase/my-orders";
        }

        Purchase purchase = purchaseOpt.get();

        // Check if this purchase belongs to the logged-in user
        if (!purchase.getUser().getId().equals(user.getId())) {
            return "redirect:/purchase/my-orders";
        }

        model.addAttribute("purchase", purchase);

        return "user/order-details";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart/view";
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Map<Product, Integer> cartItems = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Product> productOpt = productService.findById(entry.getKey());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                // Check stock availability
                if (!productService.canPurchase(entry.getKey(), entry.getValue())) {
                    redirectAttributes.addFlashAttribute("error",
                            "Insufficient stock for " + product.getName() + ". Available: "
                                    + product.getStockQuantity());
                    return "redirect:/cart/view";
                }

                cartItems.put(product, entry.getValue());
                totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(entry.getValue())));
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("user", user);

        return "user/checkout";
    }

    @PostMapping("/confirm-order")
    public String confirmOrder(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart/view";
        }

        int totalItemsPurchased = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Product> productOpt = productService.findById(entry.getKey());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                Integer quantity = entry.getValue();

                // Double-check stock availability
                if (!productService.canPurchase(entry.getKey(), quantity)) {
                    redirectAttributes.addFlashAttribute("error",
                            "Stock changed for " + product.getName() + ". Please update your cart.");
                    return "redirect:/cart/view";
                }

                // Calculate total price for this item
                BigDecimal itemTotalPrice = product.getPrice().multiply(new BigDecimal(quantity));

                // Create purchase record
                Purchase purchase = new Purchase();
                purchase.setUser(user);
                purchase.setProduct(product);
                purchase.setQuantity(quantity);
                purchase.setTotalPrice(itemTotalPrice);
                purchase.setStatus("CONFIRMED");

                // Save purchase
                purchaseService.save(purchase);

                // Update product stock
                productService.updateStock(entry.getKey(), quantity);

                totalItemsPurchased += quantity;
            }
        }

        // Clear cart after successful purchase
        session.removeAttribute("cart");

        redirectAttributes.addFlashAttribute("success",
                "Order confirmed! " + totalItemsPurchased + " items purchased successfully.");

        return "redirect:/purchase/my-orders";
    }
}
