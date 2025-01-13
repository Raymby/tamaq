package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.Order;
import com.example.tamaq.Entities.User;
import com.example.tamaq.Services.CartService;
import com.example.tamaq.Services.OrderService;
import com.example.tamaq.Services.PayPalService;
import com.example.tamaq.Services.UserService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PayPalService paypalService;

    // Перенаправление на страницу оформления заказа
    @GetMapping("/checkout")
    public String checkoutPage(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("errorMessage", "Необходима аутентификация для оформления заказа.");
            return "login";
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            model.addAttribute("errorMessage", "Пользователь не найден.");
            return "login";
        }
        List<CartItem> cartItems = cartService.getCartItems(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user);

        BigDecimal totalPrice = cartService.calculateTotalPrice(user); // Расчёт итоговой суммы
        model.addAttribute("totalPrice", totalPrice); // Добавление суммы в модель

        return "checkout";
    }

    // Оформление заказа (checkout)
    @PostMapping("/checkout")
    public String checkout(Principal principal, String phone, String address, String deliveryDate, String deliveryTime, String comments, Model model) {
        System.out.println("Delivery Time: " + deliveryTime);
        if (principal == null) {
            model.addAttribute("errorMessage", "Необходима аутентификация для оформления заказа.");
            return "login";
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            model.addAttribute("errorMessage", "Пользователь не найден.");
            return "login";
        }
        try {
            // Создание заказа
            Order order = orderService.createOrder(user, phone, address, deliveryDate, deliveryTime, comments);
            // Логика для перенаправления на PayPal
            // Получение суммы в KZT
            double totalAmountKZT = order.getTotalAmount();

            // Конвертация из KZT в USD
            double exchangeRate = 0.0022; // Пример фиксированного курса: 1 KZT = 0.0022 USD
            double totalAmount = totalAmountKZT * exchangeRate;

            // Округление до двух знаков после запятой
            totalAmount = Math.round(totalAmount * 100.0) / 100.0;

            String currency = "USD";// Установить нужную валюту

            System.out.println("Создание платежа: сумма = " + totalAmount + ", валюта = " + currency);

            // Создаем URL для успешной оплаты и отмены
            String successUrl = "http://localhost:8080/paypal/success?orderId=" + order.getId();
            String cancelUrl = "http://localhost:8080/paypal/cancel?orderId=" + order.getId();
            // Создаем платеж
            Payment payment = paypalService.createPayment(totalAmount, currency, successUrl, cancelUrl);
            // Перебираем ссылки в ответе PayPal и находим ссылку для перенаправления пользователя
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();  // Перенаправление на PayPal для подтверждения
                }
            }
            model.addAttribute("errorMessage", "Не удалось создать платеж через PayPal.");
            return "cart";  // Возвращаем пользователя на страницу корзины в случае ошибки
        } catch (PayPalRESTException e) {
            model.addAttribute("errorMessage", "Ошибка при обработке платежа через PayPal: " + e.getMessage());
            return "cart";  // Возвращаем пользователя на страницу корзины в случае ошибки
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "cart";  // Возвращаем пользователя на страницу корзины в случае ошибки с заказом
        }
    }

    // Отображение истории заказов
    @GetMapping("/orders")
    public String viewOrders(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order-history";
    }

    // Отображение деталей заказа
    @GetMapping("/order/details/{id}")
    public String getOrderDetails(@PathVariable Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }
        Order order = orderService.getOrderById(id);
        if (order == null || !order.getUser().equals(user)) {
            model.addAttribute("errorMessage", "Заказ не найден или вы не имеете доступа к этому заказу.");
            return "error";
        }
        model.addAttribute("order", order);
        return "order-details"; // Название шаблона для деталей заказа
    }
}
