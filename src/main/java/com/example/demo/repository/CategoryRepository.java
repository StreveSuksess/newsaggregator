package com.example.demo.repository;
import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    @Query("SELECT c FROM Category c ORDER BY c.name")
    List<Category> findAllOrdered();
    @PostConstruct
    default void logCategories() {
        List<Category> categories = findAll();
        System.out.println("Available categories in database: " + 
            categories.stream()
                .map(Category::getName)
                .toList());
    }
} 