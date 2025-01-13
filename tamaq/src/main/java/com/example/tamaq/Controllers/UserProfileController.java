package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.Order;
import com.example.tamaq.Entities.User;
import com.example.tamaq.Services.OrderService;
import com.example.tamaq.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final Logger logger = LogManager.getLogger(UserProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService; // Инъекция OrderService

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.info("Получение профиля пользователя: {}", username);

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("Пользователь не найден: {}", username);
            return "error"; // или другая страница ошибки
        }

        // Получение списка заказов для текущего пользователя
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders); // Передача заказов в модель

        model.addAttribute("user", user);
        logger.info("Данные профиля и заказы загружены для пользователя: {}", user.getUsername());
        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.info("Редактирование профиля пользователя: {}", username);

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("Пользователь не найден: {}", username);
            return "error"; // или другая страница ошибки
        }

        model.addAttribute("user", user);
        logger.info("Форма редактирования загружена для пользователя: {}", user.getUsername());
        return "edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Попытка обновить профиль пользователя: {}", currentUsername);

        try {
            // Получение текущего пользователя
            User currentUser = userService.findByUsername(currentUsername);
            if (currentUser == null) {
                logger.error("Пользователь с именем {} не найден.", currentUsername);
                model.addAttribute("error", "Пользователь не найден.");
                return "edit-profile";
            }

            // Обновление данных
            currentUser.setContactNumber(user.getContactNumber());
            currentUser.setEmail(user.getEmail());
            currentUser.setBankCard(user.getBankCard());
            userService.updateUser(currentUser);

            logger.info("Профиль пользователя успешно обновлён: {}", currentUsername);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении профиля пользователя {}: {}", currentUsername, e.getMessage());
            model.addAttribute("error", "Произошла ошибка при обновлении профиля.");
            return "edit-profile";
        }

        return "redirect:/profile";
    }
}
