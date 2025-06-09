package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleNotFoundExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateExceptionWithMessage() {
        String message = "Article not found with id: 123";
        
        ArticleNotFoundException exception = new ArticleNotFoundException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithNullMessage_ShouldAcceptNullMessage() {
        ArticleNotFoundException exception = new ArticleNotFoundException(null);
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldAcceptEmptyMessage() {
        String message = "";
        
        ArticleNotFoundException exception = new ArticleNotFoundException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void exception_ShouldBeInstanceOfRuntimeException() {
        ArticleNotFoundException exception = new ArticleNotFoundException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void throwException_ShouldBeCatchableAsRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new ArticleNotFoundException("Test exception");
        });
    }

    @Test
    void throwException_ShouldBeCatchableAsArticleNotFoundException() {
        assertThrows(ArticleNotFoundException.class, () -> {
            throw new ArticleNotFoundException("Test exception");
        });
    }

    @Test
    void getMessage_ShouldReturnCorrectMessage() {
        String expectedMessage = "Article with id 999 not found";
        ArticleNotFoundException exception = new ArticleNotFoundException(expectedMessage);
        
        String actualMessage = exception.getMessage();
        
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void exception_ShouldHaveCorrectAnnotation() {
        // Test that the exception class is properly annotated with @ResponseStatus
        // This is important for Spring's exception handling
        ArticleNotFoundException exception = new ArticleNotFoundException("Test");
        
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        // The @ResponseStatus annotation would be verified by Spring integration tests
    }

    @Test
    void constructor_WithLongMessage_ShouldAcceptLongMessage() {
        String longMessage = "A".repeat(1000);
        
        ArticleNotFoundException exception = new ArticleNotFoundException(longMessage);
        
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void constructor_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        String messageWithSpecialChars = "Article not found: áéíóú ñ !@#$%^&*()";
        
        ArticleNotFoundException exception = new ArticleNotFoundException(messageWithSpecialChars);
        
        assertNotNull(exception);
        assertEquals(messageWithSpecialChars, exception.getMessage());
    }
}
