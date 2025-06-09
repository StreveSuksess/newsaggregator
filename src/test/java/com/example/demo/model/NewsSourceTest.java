package com.example.demo.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class NewsSourceTest {
    @Test
    void testClass() {
        assertNotNull(NewsSource.class);
    }
}
