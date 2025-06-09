package com.example.demo.repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class KeywordRepositoryTest {
    @Test
    void testClass() {
        assertNotNull(KeywordRepository.class);
    }
}
