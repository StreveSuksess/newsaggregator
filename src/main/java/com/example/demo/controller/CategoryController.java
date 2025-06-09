package com.example.demo.controller;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Collections;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository repo;
    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }
    @GetMapping
    public List<Category> getAll() {
        List<Category> categories = repo.findAll();
        return categories != null ? categories : Collections.emptyList();
    }
} 