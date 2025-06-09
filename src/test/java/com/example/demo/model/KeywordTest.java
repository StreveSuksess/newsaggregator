package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KeywordTest {

    @Test
    void constructor_ShouldCreateKeywordWithDefaults() {
        Keyword keyword = new Keyword();
        
        assertNotNull(keyword);
        assertNull(keyword.getId());
        assertNull(keyword.getName());
        assertNull(keyword.getType());
        assertNotNull(keyword.getArticles());
        assertTrue(keyword.getArticles().isEmpty());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Keyword keyword = new Keyword();
        
        Article article1 = new Article();
        article1.setId(1L);
        
        Article article2 = new Article();
        article2.setId(2L);
        
        Set<Article> articles = Set.of(article1, article2);

        keyword.setId(1L);
        keyword.setName("java");
        keyword.setType(KeywordType.CONCEPT);
        keyword.setArticles(articles);

        assertEquals(1L, keyword.getId());
        assertEquals("java", keyword.getName());
        assertEquals(KeywordType.CONCEPT, keyword.getType());
        assertEquals(articles, keyword.getArticles());
    }

    @Test
    void setName_WithEmptyString_ShouldAcceptEmptyString() {
        Keyword keyword = new Keyword();
        
        keyword.setName("");
        
        assertEquals("", keyword.getName());
    }

    @Test
    void setName_WithLongName_ShouldAcceptLongName() {
        Keyword keyword = new Keyword();
        String longName = "Very long keyword name ".repeat(10);
        
        keyword.setName(longName);
        
        assertEquals(longName, keyword.getName());
    }

    @Test
    void setType_WithAllKeywordTypes_ShouldAcceptAll() {
        Keyword keyword = new Keyword();
        
        // Test CONCEPT
        keyword.setType(KeywordType.CONCEPT);
        assertEquals(KeywordType.CONCEPT, keyword.getType());
        
        // Test PERSON
        keyword.setType(KeywordType.PERSON);
        assertEquals(KeywordType.PERSON, keyword.getType());
        
        // Test LOCATION
        keyword.setType(KeywordType.LOCATION);
        assertEquals(KeywordType.LOCATION, keyword.getType());
        
        // Test ORGANIZATION
        keyword.setType(KeywordType.ORGANIZATION);
        assertEquals(KeywordType.ORGANIZATION, keyword.getType());
        
        // Test EVENT
        keyword.setType(KeywordType.EVENT);
        assertEquals(KeywordType.EVENT, keyword.getType());
    }

    @Test
    void setArticles_WithEmptySet_ShouldAcceptEmptySet() {
        Keyword keyword = new Keyword();
        Set<Article> emptySet = new HashSet<>();
        
        keyword.setArticles(emptySet);
        
        assertEquals(emptySet, keyword.getArticles());
        assertTrue(keyword.getArticles().isEmpty());
    }

    @Test
    void setArticles_WithNull_ShouldAcceptNull() {
        Keyword keyword = new Keyword();
        
        keyword.setArticles(null);
        
        assertNull(keyword.getArticles());
    }

    @Test
    void setArticles_WithMultipleArticles_ShouldAcceptAll() {
        Keyword keyword = new Keyword();
        
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("First Article");
        
        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Second Article");
        
        Article article3 = new Article();
        article3.setId(3L);
        article3.setTitle("Third Article");
        
        Set<Article> articles = Set.of(article1, article2, article3);
        
        keyword.setArticles(articles);
        
        assertEquals(articles, keyword.getArticles());
        assertEquals(3, keyword.getArticles().size());
    }

    @Test
    void setName_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        Keyword keyword = new Keyword();
        String nameWithSpecialChars = "Spring Boot & JPA!@#$%^&*()";
        
        keyword.setName(nameWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, keyword.getName());
    }

    @Test
    void setId_WithZero_ShouldAcceptZero() {
        Keyword keyword = new Keyword();
        
        keyword.setId(0L);
        
        assertEquals(0L, keyword.getId());
    }

    @Test
    void setId_WithNegative_ShouldAcceptNegative() {
        Keyword keyword = new Keyword();
        
        keyword.setId(-1L);
        
        assertEquals(-1L, keyword.getId());
    }

    @Test
    void setId_WithLargeNumber_ShouldAcceptLargeNumber() {
        Keyword keyword = new Keyword();
        
        keyword.setId(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, keyword.getId());
    }

    @Test
    void setName_WithNull_ShouldAcceptNull() {
        Keyword keyword = new Keyword();
        
        keyword.setName(null);
        
        assertNull(keyword.getName());
    }

    @Test
    void setType_WithNull_ShouldAcceptNull() {
        Keyword keyword = new Keyword();
        
        keyword.setType(null);
        
        assertNull(keyword.getType());
    }

    @Test
    void setId_WithNull_ShouldAcceptNull() {
        Keyword keyword = new Keyword();
        
        keyword.setId(null);
        
        assertNull(keyword.getId());
    }

    @Test
    void setName_WithWhitespace_ShouldAcceptWhitespace() {
        Keyword keyword = new Keyword();
        String whitespace = "   ";
        
        keyword.setName(whitespace);
        
        assertEquals(whitespace, keyword.getName());
    }

    @Test
    void setName_WithUnicodeCharacters_ShouldAcceptUnicode() {
        Keyword keyword = new Keyword();
        String unicode = "关键词 🚀 キーワード";
        
        keyword.setName(unicode);
        
        assertEquals(unicode, keyword.getName());
    }
}
