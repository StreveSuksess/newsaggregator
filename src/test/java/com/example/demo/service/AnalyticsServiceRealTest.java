package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.KeywordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceRealTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getArticlesCountByCategory_ShouldReturnCorrectCounts() {
        // Arrange
        Category category1 = new Category();
        category1.setName("Tech");
        
        Category category2 = new Category();
        category2.setName("News");

        Article article1 = new Article();
        article1.setCategory(category1);
        
        Article article2 = new Article();
        article2.setCategory(category1);
        
        Article article3 = new Article();
        article3.setCategory(category2);

        List<Article> articles = Arrays.asList(article1, article2, article3);
        when(articleRepository.findAll()).thenReturn(articles);

        // Act
        Map<String, Long> result = analyticsService.getArticlesCountByCategory();

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.get("Tech"));
        assertEquals(1L, result.get("News"));
        verify(articleRepository).findAll();
    }

    @Test
    void getTopKeywords_WithValidData_ShouldReturnTopKeywords() {
        // Arrange
        Keyword keyword1 = new Keyword();
        keyword1.setName("Java");
        keyword1.setType(KeywordType.CONCEPT);
        
        Keyword keyword2 = new Keyword();
        keyword2.setName("Spring");
        keyword2.setType(KeywordType.CONCEPT);

        when(keywordRepository.findAll()).thenReturn(Arrays.asList(keyword1, keyword2));
        when(articleRepository.countByKeywordsContaining(keyword1)).thenReturn(10L);
        when(articleRepository.countByKeywordsContaining(keyword2)).thenReturn(5L);

        // Act
        List<Map.Entry<String, Long>> result = analyticsService.getTopKeywords(2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getKey());
        assertEquals(10L, result.get(0).getValue());
        assertEquals("Spring", result.get(1).getKey());
        assertEquals(5L, result.get(1).getValue());
    }

    @Test
    void getTrendingArticles_ShouldReturnArticlesInDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setViews(100L);
        article.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(articleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(
            new org.springframework.data.domain.PageImpl<>(Arrays.asList(article))
        );

        // Act
        List<Article> result = analyticsService.getTrendingArticles(startDate, endDate, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Article", result.get(0).getTitle());
    }
} 