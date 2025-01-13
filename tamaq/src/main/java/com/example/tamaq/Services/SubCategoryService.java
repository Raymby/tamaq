package com.example.tamaq.Services;

import com.example.tamaq.Entities.SubCategory;
import com.example.tamaq.Repositories.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public List<SubCategory> findAll() {
        return subCategoryRepository.findAll();
    }

    public SubCategory findById(Long id) {
        return subCategoryRepository.findById(id).orElse(null);
    }

    public SubCategory findByName(String name) {
        return subCategoryRepository.findByName(name);
    }

    public SubCategory save(SubCategory subCategory) {
        return subCategoryRepository.save(subCategory);
    }
}
