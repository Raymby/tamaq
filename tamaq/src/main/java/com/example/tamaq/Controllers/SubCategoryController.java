package com.example.tamaq.Controllers;

import com.example.tamaq.Entities.Product;
import com.example.tamaq.Entities.SubCategory;
import com.example.tamaq.Services.CategoryService;
import com.example.tamaq.Services.ProductService;
import com.example.tamaq.Services.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subcategories")
public class SubCategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public String listProductsBySubCategory(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {
        SubCategory subCategory = subCategoryService.findById(id);
        if (subCategory != null) {
            List<Product> products = productService.filterProductsBySubCategoryCountryBrandAndPrice(id, country, brand, minPrice, maxPrice);
            List<String> availableCountries = productService.getCountriesBySubcategory(id);
            List<String> availableBrands = productService.getBrandsBySubcategory(id);

            model.addAttribute("subCategory", subCategory);
            model.addAttribute("products", products);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("availableCountries", availableCountries);
            model.addAttribute("availableBrands", availableBrands);
        } else {
            model.addAttribute("errorMessage", "Подкатегория не найдена");
            return "error";
        }
        return "subcategory-products";
    }
    }
