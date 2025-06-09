package com.example.demo.controller;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryRepository repo;
    @InjectMocks
    private CategoryController controller;
    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        List<Category> result = controller.getAll();
        assertNotNull(result);
    }
} 