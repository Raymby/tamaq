package com.example.tamaq.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String fullDescription;
    private Double price;

    // Новые поля
    private String brand;
    private String country;
    private Double discount; // Количество скидки, например 10 или 15% (например, 0.1 = 10%)

    private Boolean hasDiscount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    private String imgUrl; // Новое поле для хранения ссылки на изображение

    // Конструктор по умолчанию
    public Product() {}

    // Конструктор с параметрами
    public Product(String name, String description, Double price, Category category, String country) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.country = country;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SubCategory getSubCategory() { // Проверьте наличие этого метода
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) { // Проверьте наличие этого метода
        this.subCategory = subCategory;
    }

    public String getCountry() {
        return country;  // Make sure it's returning a String
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public boolean getHasDiscount() {
        return hasDiscount != null && hasDiscount; // Проверка на null и возврат значения
    }

    public String getFullDescription() {
        return fullDescription;
    }
    public Double getDiscountPrice() {
        if (Boolean.TRUE.equals(hasDiscount) && discount != null && discount > 0) {
            return price - (price * discount); // Вычисляем цену со скидкой
        }
        return price; // Если скидки нет, возвращаем обычную цену
    }

}
