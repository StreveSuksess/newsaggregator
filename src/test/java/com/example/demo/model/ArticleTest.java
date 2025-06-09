package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void constructor_ShouldCreateArticleWithDefaults() {
        Article article = new Article();
        
        assertNotNull(article);
        assertNull(article.getId());
        assertNull(article.getUrl());
        assertNull(article.getTitle());
        assertNull(article.getContent());
        assertNull(article.getSummary());
        assertNull(article.getSource());
        assertNull(article.getCategory());
        assertNull(article.getCreatedAt());
        assertNull(article.getImgLinks());
        assertEquals(0L, article.getViews()); // Default value
        assertNotNull(article.getKeywords());
        assertTrue(article.getKeywords().isEmpty()); // Empty set by default
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Article article = new Article();
        LocalDateTime now = LocalDateTime.now();
        
        NewsSource source = new NewsSource();
        source.setId(1L);
        source.setName("Test Source");
        
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        
        List<String> imgLinks = List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg");
        
        Keyword keyword = new Keyword();
        keyword.setId(1L);
        keyword.setName("test");
        Set<Keyword> keywords = Set.of(keyword);

        article.setId(1L);
        article.setUrl("http://example.com/article");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(now);
        article.setImgLinks(imgLinks);
        article.setViews(100L);
        article.setKeywords(keywords);

        assertEquals(1L, article.getId());
        assertEquals("http://example.com/article", article.getUrl());
        assertEquals("Test Article", article.getTitle());
        assertEquals("Test Content", article.getContent());
        assertEquals("Test Summary", article.getSummary());
        assertEquals(source, article.getSource());
        assertEquals(category, article.getCategory());
        assertEquals(now, article.getCreatedAt());
        assertEquals(imgLinks, article.getImgLinks());
        assertEquals(100L, article.getViews());
        assertEquals(keywords, article.getKeywords());
    }

    @Test
    void setKeywords_WithNull_ShouldAcceptNull() {
        Article article = new Article();
        
        article.setKeywords(null);
        
        assertNull(article.getKeywords());
    }

    @Test
    void setKeywords_WithEmptySet_ShouldAcceptEmptySet() {
        Article article = new Article();
        Set<Keyword> emptySet = new HashSet<>();
        
        article.setKeywords(emptySet);
        
        assertEquals(emptySet, article.getKeywords());
        assertTrue(article.getKeywords().isEmpty());
    }

    @Test
    void setKeywords_WithMultipleKeywords_ShouldAcceptAll() {
        Article article = new Article();
        
        Keyword keyword1 = new Keyword();
        keyword1.setId(1L);
        keyword1.setName("java");
        
        Keyword keyword2 = new Keyword();
        keyword2.setId(2L);
        keyword2.setName("spring");
        
        Set<Keyword> keywords = Set.of(keyword1, keyword2);
        
        article.setKeywords(keywords);
        
        assertEquals(keywords, article.getKeywords());
        assertEquals(2, article.getKeywords().size());
    }

    @Test
    void setViews_WithZero_ShouldAcceptZero() {
        Article article = new Article();
        
        article.setViews(0L);
        
        assertEquals(0L, article.getViews());
    }

    @Test
    void setViews_WithNegative_ShouldAcceptNegative() {
        Article article = new Article();
        
        article.setViews(-1L);
        
        assertEquals(-1L, article.getViews());
    }

    @Test
    void setViews_WithLargeNumber_ShouldAcceptLargeNumber() {
        Article article = new Article();
        
        article.setViews(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, article.getViews());
    }

    @Test
    void setImgLinks_WithEmptyList_ShouldAcceptEmptyList() {
        Article article = new Article();
        List<String> emptyList = List.of();
        
        article.setImgLinks(emptyList);
        
        assertEquals(emptyList, article.getImgLinks());
        assertTrue(article.getImgLinks().isEmpty());
    }

    @Test
    void setImgLinks_WithMultipleLinks_ShouldAcceptAll() {
        Article article = new Article();
        List<String> links = List.of(
            "http://example.com/image1.jpg",
            "http://example.com/image2.png",
            "https://test.com/photo.gif"
        );
        
        article.setImgLinks(links);
        
        assertEquals(links, article.getImgLinks());
        assertEquals(3, article.getImgLinks().size());
    }

    @Test
    void setCreatedAt_WithFutureDate_ShouldAcceptFutureDate() {
        Article article = new Article();
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        
        article.setCreatedAt(future);
        
        assertEquals(future, article.getCreatedAt());
    }

    @Test
    void setCreatedAt_WithPastDate_ShouldAcceptPastDate() {
        Article article = new Article();
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        
        article.setCreatedAt(past);
        
        assertEquals(past, article.getCreatedAt());
    }

    @Test
    void setUrl_WithLongUrl_ShouldAcceptLongUrl() {
        Article article = new Article();
        String longUrl = "http://example.com/" + "a".repeat(2000);
        
        article.setUrl(longUrl);
        
        assertEquals(longUrl, article.getUrl());
    }

    @Test
    void setTitle_WithLongTitle_ShouldAcceptLongTitle() {
        Article article = new Article();
        String longTitle = "A".repeat(1000);
        
        article.setTitle(longTitle);
        
        assertEquals(longTitle, article.getTitle());
    }

    @Test
    void setContent_WithLongContent_ShouldAcceptLongContent() {
        Article article = new Article();
        String longContent = "Content ".repeat(1000);
        
        article.setContent(longContent);
        
        assertEquals(longContent, article.getContent());
    }
}
