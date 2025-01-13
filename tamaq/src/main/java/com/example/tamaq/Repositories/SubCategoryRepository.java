package com.example.tamaq.Repositories;

import com.example.tamaq.Entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    SubCategory findByName(String name);
}
