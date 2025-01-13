package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.*;
import com.example.tamaq.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Отображение формы регистрации");
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        logger.info("Попытка регистрации пользователя с именем: {}", user.getUsername());
        try {
            userService.save(user);
            logger.info("Пользователь успешно зарегистрирован: {}", user.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя: {}", user.getUsername(), e);
            model.addAttribute("error", "Регистрация не удалась. Попробуйте снова.");
            return "registration"; // Вернем на страницу регистрации с ошибкой
        }
    }


    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.info("Загрузка главной страницы для пользователя: {}", username);

        model.addAttribute("userName", username);

        try {
            // Получаем все категории
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            // Добавляем подкатегории для каждой категории в модель
            model.addAttribute("subCategories", categories.stream()
                    .flatMap(category -> category.getSubCategories().stream())
                    .toList());

            // Получаем все продукты и добавляем в модель
            model.addAttribute("products", productService.getAllProducts());

            // Получаем продукты из подкатегории "Яблоки"
            SubCategory applesSubCategory = subCategoryService.findByName("Яблоки");
            List<Product> appleProducts = productService.findBySubCategory(applesSubCategory);
            model.addAttribute("appleProducts", appleProducts);

            // Получаем продукты из подкатегории "Морковь"
            SubCategory tomatoesSubCategory = subCategoryService.findByName("Морковь");
            List<Product> tomatoProducts = productService.findBySubCategory(tomatoesSubCategory);
            model.addAttribute("tomatoProducts", tomatoProducts);

            // Получаем продукты со скидками
            List<Product> discountProducts = productService.findByHasDiscountTrue();
            model.addAttribute("discountProducts", discountProducts);

            logger.info("Главная страница успешно загружена для пользователя: {}", username);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке главной страницы для пользователя: {}", username, e);
            model.addAttribute("error", "Не удалось загрузить главную страницу.");
        }

        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "login";
    }


    @GetMapping("/logout")
    public String logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Пользователь '{}' вышел из системы", username);
        return "redirect:/login";
    }

    @GetMapping("/discounts")
    public String showDiscountProducts(Model model) {
        logger.info("Отображение продуктов со скидками");
        try {
            List<Product> discountedProducts = productService.getDiscountedProducts();
            model.addAttribute("discountProducts", discountedProducts);
            logger.info("Продукты со скидками успешно загружены");
        } catch (Exception e) {
            logger.error("Ошибка при загрузке продуктов со скидками", e);
            model.addAttribute("error", "Не удалось загрузить продукты со скидками.");
        }
        return "discounts";
    }

}
