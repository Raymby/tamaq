package com.example.tamaq.Repositories;

import com.example.tamaq.Entities.Category;
import com.example.tamaq.Entities.Product;
import com.example.tamaq.Entities.SubCategory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategory(Category category);

    // Поиск продуктов по подкатегории
    List<Product> findBySubCategory(SubCategory subCategory);

    List<Product> findByHasDiscountTrue();

    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :subcategoryId AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findProductsBySubcategoryAndPrice(
            @Param("subcategoryId") Long subcategoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :subcategoryId " +
            "AND (:country IS NULL OR :country = '' OR p.country = :country) " +
            "AND (:brand IS NULL OR :brand = '' OR p.brand = :brand) " + // Добавлено условие для бренда
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findProductsBySubcategoryCountryBrandAndPrice(
            @Param("subcategoryId") Long subcategoryId,
            @Param("country") String country,
            @Param("brand") String brand,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    @Query("SELECT DISTINCT p.country FROM Product p WHERE p.subCategory.id = :subcategoryId")
    List<String> findDistinctCountriesBySubcategoryId(@Param("subcategoryId") Long subcategoryId);

    // Метод для получения уникальных брендов продуктов определенной подкатегории
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.subCategory.id = :subcategoryId AND p.brand IS NOT NULL")
    List<String> getBrandsBySubcategoryId(@Param("subcategoryId") Long subcategoryId);

    @Query("SELECT p FROM Product p WHERE p.hasDiscount = true")
    List<Product> findDiscountedProducts();


}
