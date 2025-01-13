package com.example.tamaq.Services;

import com.example.tamaq.Entities.Category;
import com.example.tamaq.Entities.Product;
import com.example.tamaq.Entities.SubCategory;
import com.example.tamaq.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findBySubCategory(SubCategory subCategory) {
        return productRepository.findBySubCategory(subCategory);
    }

    public List<String> getCountriesBySubcategory(Long subcategoryId) {
        return productRepository.findDistinctCountriesBySubcategoryId(subcategoryId);
    }

    public List<Product> filterProductsBySubCategoryCountryBrandAndPrice(Long subcategoryId, String country, String brand, Double minPrice, Double maxPrice) {
        return productRepository.findProductsBySubcategoryCountryBrandAndPrice(subcategoryId, country, brand, minPrice, maxPrice);
    }

    public List<String> getBrandsBySubcategory(Long subcategoryId) {
        return productRepository.getBrandsBySubcategoryId(subcategoryId);
    }

    public List<Product> findByHasDiscountTrue() {
        return productRepository.findByHasDiscountTrue();
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findDiscountedProducts();
    }

}
