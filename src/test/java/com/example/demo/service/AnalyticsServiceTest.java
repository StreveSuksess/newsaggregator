package com.example.demo.service;

import com.example.demo.dto.AnalyticsResponse;
import com.example.demo.dto.ArticleFilterRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void filterArticles_ShouldReturnPagedResults() {
        ArticleFilterRequest request = new ArticleFilterRequest();
        request.setPage(0);
        request.setSize(10);
        
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        
        Page<Article> result = analyticsService.filterArticles(request);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchArticles_ShouldReturnPagedResults() {
        ArticleFilterRequest request = new ArticleFilterRequest();
        request.setSearchInTitle(true);
        request.setSearchInContent(true);
        
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        
        when(articleRepository.searchArticles(anyString(), any(), any(), anyBoolean(), anyBoolean(), any(Pageable.class)))
            .thenReturn(page);
        
        Page<Article> result = analyticsService.searchArticles("test", request);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).searchArticles(anyString(), any(), any(), anyBoolean(), anyBoolean(), any(Pageable.class));
    }

    @Test
    void getTrendingArticles_ShouldReturnTrendingArticles() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        
        List<Article> result = analyticsService.getTrendingArticles(start, end, 10);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getArticlesCountByDateRange_ShouldReturnDateCounts() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        Article article = createTestArticle();
        when(articleRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(article));
        
        Map<String, Long> result = analyticsService.getArticlesCountByDateRange(start, end);
        
        assertNotNull(result);
        verify(articleRepository).findAll(any(Specification.class));
    }

    @Test
    void getTopKeywords_ShouldReturnTopKeywords() {
        Keyword keyword1 = new Keyword();
        keyword1.setName("Java");
        keyword1.setType(KeywordType.CONCEPT);
        
        Keyword keyword2 = new Keyword();
        keyword2.setName("Spring");
        keyword2.setType(KeywordType.CONCEPT);

        when(keywordRepository.findAll()).thenReturn(Arrays.asList(keyword1, keyword2));
        when(articleRepository.countByKeywordsContaining(any())).thenReturn(10L, 5L);

        List<Map.Entry<String, Long>> result = analyticsService.getTopKeywords(2);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(keywordRepository).findAll();
    }

    @Test
    void getTopKeywords_WithEmptyKeywords_ShouldReturnEmptyList() {
        when(keywordRepository.findAll()).thenReturn(Collections.emptyList());

        List<Map.Entry<String, Long>> result = analyticsService.getTopKeywords(10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(keywordRepository).findAll();
    }

    @Test
    void getTopKeywords_WithException_ShouldReturnEmptyList() {
        when(keywordRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        List<Map.Entry<String, Long>> result = analyticsService.getTopKeywords(10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(keywordRepository).findAll();
    }

    @Test
    void getTopPersonalities_ShouldReturnPersonalities() {
        Keyword personality = new Keyword();
        personality.setName("John Doe");
        personality.setType(KeywordType.PERSON);

        when(keywordRepository.findByType(KeywordType.PERSON)).thenReturn(List.of(personality));
        when(articleRepository.countByKeywordsContaining(any())).thenReturn(5L);

        List<Map.Entry<String, Long>> result = analyticsService.getTopPersonalities(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(keywordRepository).findByType(KeywordType.PERSON);
    }

    @Test
    void getArticlesCountByCategory_ShouldReturnCategoryCounts() {
        Category category = new Category();
        category.setName("Tech");
        
        Article article = createTestArticle();
        article.setCategory(category);

        when(articleRepository.findAll()).thenReturn(List.of(article));

        Map<String, Long> result = analyticsService.getArticlesCountByCategory();

        assertNotNull(result);
        assertEquals(1L, result.get("Tech"));
        verify(articleRepository).findAll();
    }

    @Test
    void getKeywordOccurrences_ShouldReturnOccurrences() {
        Article article = createTestArticle();
        Keyword keyword = new Keyword();
        keyword.setName("test");
        article.setKeywords(Set.of(keyword));

        when(articleRepository.findAll()).thenReturn(List.of(article));

        Map<String, Long> result = analyticsService.getKeywordOccurrences(List.of("test"), 30);

        assertNotNull(result);
        assertEquals(1L, result.get("test"));
        verify(articleRepository).findAll();
    }

    @Test
    void getAnalytics_ShouldReturnCompleteAnalytics() {
        // Setup test data
        Category category = new Category();
        category.setName("Tech");
        
        Article article = createTestArticle();
        article.setCategory(category);
        
        Keyword keyword = new Keyword();
        keyword.setName("Java");
        keyword.setType(KeywordType.CONCEPT);

        // Mock repository calls
        when(articleRepository.findAll()).thenReturn(List.of(article));
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(article)));
        when(keywordRepository.findAll()).thenReturn(List.of(keyword));
        when(articleRepository.countByKeywordsContaining(any())).thenReturn(5L);

        AnalyticsResponse result = analyticsService.getAnalytics();

        assertNotNull(result);
        assertTrue(result.getTotalArticles() >= 0);
        assertNotNull(result.getCategoryStats());
        assertNotNull(result.getTopKeywords());
        assertNotNull(result.getTrendingArticles());
    }

    @Test
    void getAnalytics_WithException_ShouldThrowRuntimeException() {
        when(articleRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> analyticsService.getAnalytics());
    }

    private Article createTestArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test content");
        article.setSummary("Test summary");
        article.setUrl("http://test.com");
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(100L);
        return article;
    }
}
