package com.example.demo.service;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryInitializationService {
    private final CategoryRepository categoryRepository;
    @PostConstruct
    public void logCategories() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Available categories in database: {}", 
            categories.stream()
                .map(Category::getName)
                .toList());
        if (categories.isEmpty()) {
            log.warn("No categories found in database! This might indicate a problem with database initialization.");
        }
    }
} 