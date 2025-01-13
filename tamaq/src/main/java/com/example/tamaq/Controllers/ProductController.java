package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.Category;
import com.example.tamaq.Entities.Product;
import com.example.tamaq.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Правильный импорт
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);

        if (product != null) {
            if (product.getHasDiscount()) {
                int discountPercentage = (int) (product.getDiscount() * 100);
                model.addAttribute("discountPercentage", discountPercentage);
            }
            model.addAttribute("product", product);
            return "product-details"; // Название шаблона для отображения деталей продукта
        } else {
            model.addAttribute("errorMessage", "Продукт не найден");
            return "error"; // Название шаблона для ошибки
        }
    }



}
