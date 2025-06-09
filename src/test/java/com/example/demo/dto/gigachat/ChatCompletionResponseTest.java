package com.example.demo.dto.gigachat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class ChatCompletionResponseTest {
    @Test
    void testClass() {
        assertNotNull(ChatCompletionResponse.class);
    }
}
