package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.Category;
import com.example.tamaq.Services.CategoryService;
import com.example.tamaq.Services.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories"; // Название шаблона для категорий
    }

    @GetMapping("/{id}")
    public String listSubCategoriesByCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.findById(id);
        if (category != null) {
            model.addAttribute("category", category);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("subCategories", category.getSubCategories());
        } else {
            // Обработка случая, когда категория не найдена
            model.addAttribute("errorMessage", "Категория не найдена");
            return "error"; // Название шаблона для ошибки
        }
        return "category-subcategories"; // Название шаблона для подкатегорий
    }
}