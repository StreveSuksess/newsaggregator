package com.example.demo.dto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class ArticleFilterRequestTest {
    @Test
    void testClass() {
        assertNotNull(ArticleFilterRequest.class);
    }
}
