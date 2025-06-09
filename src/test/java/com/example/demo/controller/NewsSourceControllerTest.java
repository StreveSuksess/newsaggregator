package com.example.demo.controller;
import com.example.demo.model.NewsSource;
import com.example.demo.repository.NewsSourceRepository;
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
class NewsSourceControllerTest {
    @Mock
    private NewsSourceRepository repo;
    @InjectMocks
    private NewsSourceController controller;
    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        List<NewsSource> result = controller.getAll();
        assertNotNull(result);
    }
    @Test
    void getAllNames() {
        NewsSource source = new NewsSource();
        source.setName("Test");
        when(repo.findAll()).thenReturn(List.of(source));
        List<String> result = controller.getAllNames();
        assertNotNull(result);
    }
} 