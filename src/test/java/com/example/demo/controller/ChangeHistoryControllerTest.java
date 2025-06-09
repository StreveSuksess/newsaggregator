package com.example.demo.controller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class ChangeHistoryControllerTest {
    @Test
    void testClass() {
        assertNotNull(ChangeHistoryController.class);
    }
}
