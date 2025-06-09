package com.example.demo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void constructor_ShouldCreateCategoryWithDefaults() {
        Category category = new Category();
        
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Category category = new Category();

        category.setId(1L);
        category.setName("Technology");

        assertEquals(1L, category.getId());
        assertEquals("Technology", category.getName());
    }

    @Test
    void setName_WithEmptyString_ShouldAcceptEmptyString() {
        Category category = new Category();
        
        category.setName("");
        
        assertEquals("", category.getName());
    }

    @Test
    void setName_WithLongName_ShouldAcceptLongName() {
        Category category = new Category();
        String longName = "Category Name ".repeat(50);
        
        category.setName(longName);
        
        assertEquals(longName, category.getName());
    }

    @Test
    void setName_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        Category category = new Category();
        String nameWithSpecialChars = "Технологии & IT!@#$%^&*()";
        
        category.setName(nameWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, category.getName());
    }

    @Test
    void setId_WithZero_ShouldAcceptZero() {
        Category category = new Category();
        
        category.setId(0L);
        
        assertEquals(0L, category.getId());
    }

    @Test
    void setId_WithNegative_ShouldAcceptNegative() {
        Category category = new Category();
        
        category.setId(-1L);
        
        assertEquals(-1L, category.getId());
    }

    @Test
    void setId_WithLargeNumber_ShouldAcceptLargeNumber() {
        Category category = new Category();
        
        category.setId(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, category.getId());
    }

    @Test
    void setName_WithNull_ShouldAcceptNull() {
        Category category = new Category();
        
        category.setName(null);
        
        assertNull(category.getName());
    }

    @Test
    void setId_WithNull_ShouldAcceptNull() {
        Category category = new Category();
        
        category.setId(null);
        
        assertNull(category.getId());
    }

    @Test
    void setName_WithWhitespace_ShouldAcceptWhitespace() {
        Category category = new Category();
        String whitespace = "   ";
        
        category.setName(whitespace);
        
        assertEquals(whitespace, category.getName());
    }

    @Test
    void setName_WithUnicodeCharacters_ShouldAcceptUnicode() {
        Category category = new Category();
        String unicode = "分类 🚀 カテゴリ";
        
        category.setName(unicode);
        
        assertEquals(unicode, category.getName());
    }
}
