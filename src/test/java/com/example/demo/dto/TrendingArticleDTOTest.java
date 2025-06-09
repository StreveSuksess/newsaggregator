package com.example.demo.dto;

import com.example.demo.model.Article;
import com.example.demo.model.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrendingArticleDTOTest {

    @Test
    void constructor_ShouldCreateEmptyDTO() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getCategoryName());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getViews());
        assertNull(dto.getSummary());
        assertNull(dto.getUrl());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setTitle("Test Article Title");
        dto.setCategoryName("Technology");
        dto.setCreatedAt(now);
        dto.setViews(500L);
        dto.setSummary("Test Summary");
        dto.setUrl("http://example.com/article");

        assertEquals(1L, dto.getId());
        assertEquals("Test Article Title", dto.getTitle());
        assertEquals("Technology", dto.getCategoryName());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(500L, dto.getViews());
        assertEquals("Test Summary", dto.getSummary());
        assertEquals("http://example.com/article", dto.getUrl());
    }

    @Test
    void fromEntity_ShouldConvertArticleToDTO() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setSummary("Test Summary");
        article.setUrl("http://example.com/test-article");
        article.setViews(1000L);
        
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        
        Category category = new Category();
        category.setName("Technology");
        article.setCategory(category);

        TrendingArticleDTO dto = TrendingArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals(article.getId(), dto.getId());
        assertEquals(article.getTitle(), dto.getTitle());
        assertEquals("Technology", dto.getCategoryName());
        assertEquals(article.getCreatedAt(), dto.getCreatedAt());
        assertEquals(article.getViews(), dto.getViews());
        assertEquals(article.getSummary(), dto.getSummary());
        assertEquals(article.getUrl(), dto.getUrl());
    }

    @Test
    void fromEntity_WithNullCategory_ShouldHaveNullCategoryName() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setCategory(null);

        TrendingArticleDTO dto = TrendingArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Article", dto.getTitle());
        assertNull(dto.getCategoryName());
    }

    @Test
    void fromEntity_WithAllNullFields_ShouldHandleGracefully() {
        Article article = new Article();
        
        TrendingArticleDTO dto = TrendingArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getCategoryName());
        assertNull(dto.getCreatedAt());
        assertEquals(0L, dto.getViews());
        assertNull(dto.getSummary());
        assertNull(dto.getUrl());
    }

    @Test
    void setViews_WithZero_ShouldAcceptZero() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        
        dto.setViews(0L);
        
        assertEquals(0L, dto.getViews());
    }

    @Test
    void setViews_WithNegativeValue_ShouldAcceptNegativeValue() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        
        dto.setViews(-1L);
        
        assertEquals(-1L, dto.getViews());
    }

    @Test
    void setViews_WithLargeValue_ShouldAcceptLargeValue() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        Long largeValue = Long.MAX_VALUE;
        
        dto.setViews(largeValue);
        
        assertEquals(largeValue, dto.getViews());
    }

    @Test
    void setTitle_WithEmptyString_ShouldAcceptEmptyString() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        
        dto.setTitle("");
        
        assertEquals("", dto.getTitle());
    }

    @Test
    void setUrl_WithValidUrl_ShouldAcceptValidUrl() {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        String url = "https://example.com/article/123";
        
        dto.setUrl(url);
        
        assertEquals(url, dto.getUrl());
    }
}
