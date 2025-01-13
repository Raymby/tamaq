package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.Product;
import com.example.tamaq.Entities.User;
import com.example.tamaq.Services.CartService;
import com.example.tamaq.Services.ProductService;
import com.example.tamaq.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @PostMapping("/cart/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam("productId") Long productId,
                                         @RequestParam("quantity") int quantity,
                                         Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findByUsername(principal.getName());
            Product product = productService.findById(productId);

            if (product != null) {
                CartItem cartItem = cartService.addToCart(product, quantity, user);
                System.out.println("CartItem ID: " + cartItem.getId()); // Отладка
                response.put("success", true);
                response.put("cartItemId", cartItem.getId());
            } else {
                response.put("success", false);
                response.put("message", "Продукт не найден");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/cart/add-direct")
    public String addToCartDirect(@RequestParam("productId") Long productId,
                                  @RequestParam("quantity") int quantity,
                                  Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            Product product = productService.findById(productId);

            if (product != null) {
                cartService.addToCart(product, quantity, user);
                redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно добавлен в корзину!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Продукт не найден.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении продукта: " + e.getMessage());
        }
        return "redirect:/products/" + productId; // Возвращает пользователя обратно на страницу продукта
    }


    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<CartItem> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);

        BigDecimal totalPrice = cartService.calculateTotalPrice(user); // Расчёт итоговой суммы
        model.addAttribute("totalPrice", totalPrice); // Добавление суммы в модель

        return "cart"; // Название шаблона для отображения корзины
    }

    @PostMapping("/remove-from-cart")
    public String removeFromCartClassic(@RequestParam("id") Long id) {
        CartItem cartItem = cartService.findById(id);
        if (cartItem != null) {
            cartService.removeItem(id);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/api/remove")
    @ResponseBody
    public Map<String, Object> removeFromCartAjax(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            CartItem cartItem = cartService.findById(id);
            if (cartItem != null) {
                cartService.removeItem(id);
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Товар не найден в корзине.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }


    @PostMapping("/cart/update-quantity")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam("id") Long id, @RequestParam("increase") boolean increase, Principal principal) {
        CartItem cartItem = cartService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + (increase ? 1 : -1);
            newQuantity = Math.max(newQuantity, 0);

            if (newQuantity > 0) {
                cartItem.setQuantity(newQuantity);
                cartService.save(cartItem);
            } else {
                cartService.removeItem(id);
                newQuantity = 0; // Установите количество в 0, если товар был удален
            }

            response.put("success", true);
            response.put("newQuantity", newQuantity);

            // Обновляем общую цену для всех товаров в корзине
            User user = userService.findByUsername(principal.getName());
            BigDecimal newTotalPrice = cartService.calculateTotalPrice(user);
            response.put("newTotalPrice", newTotalPrice); // Добавляем новую общую цену в ответ
        } else {
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/cart/summary")
    @ResponseBody
    public Map<String, Object> getCartSummary(Principal principal) {
        Map<String, Object> summary = new HashMap<>();
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            List<CartItem> cartItems = cartService.getCartItems(user);

            int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            double totalSum = cartItems.stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();

            summary.put("totalQuantity", totalQuantity);
            summary.put("totalSum", totalSum);
        } else {
            summary.put("totalQuantity", 0);
            summary.put("totalSum", 0.0);
        }
        return summary;
    }


}
