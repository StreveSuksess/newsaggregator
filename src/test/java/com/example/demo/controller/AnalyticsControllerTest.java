package com.example.demo.controller;
import com.example.demo.dto.AnalyticsResponse;
import com.example.demo.dto.ArticleFilterRequest;
import com.example.demo.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {
    @Mock
    private AnalyticsService analyticsService;
    @InjectMocks
    private AnalyticsController controller;
    @Test
    void filterArticles() {
        ArticleFilterRequest request = new ArticleFilterRequest();
        when(analyticsService.filterArticles(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        ResponseEntity<?> result = controller.filterArticles(request);
        assertNotNull(result);
    }
    @Test
    void searchArticles() {
        ArticleFilterRequest request = new ArticleFilterRequest();
        when(analyticsService.searchArticles(anyString(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        ResponseEntity<?> result = controller.searchArticles("test", request);
        assertNotNull(result);
    }
    @Test
    void getTrendingArticles() {
        when(analyticsService.getTrendingArticles(any(), any(), anyInt())).thenReturn(Collections.emptyList());
        ResponseEntity<?> result = controller.getTrendingArticles(LocalDateTime.now(), LocalDateTime.now(), 10);
        assertNotNull(result);
    }
    @Test
    void getArticlesCount() {
        when(analyticsService.getArticlesCountByDateRange(any(), any())).thenReturn(Collections.emptyMap());
        ResponseEntity<?> result = controller.getArticlesCount(LocalDateTime.now(), LocalDateTime.now());
        assertNotNull(result);
    }
    @Test
    void getKeywordTrends() {
        when(analyticsService.getKeywordTrends(any(), any(), any())).thenReturn(Collections.emptyList());
        ResponseEntity<?> result = controller.getKeywordTrends(List.of("test"), LocalDateTime.now(), LocalDateTime.now());
        assertNotNull(result);
    }
    @Test
    void getKeywordOccurrences() {
        when(analyticsService.getKeywordOccurrences(any(), anyInt())).thenReturn(Collections.emptyMap());
        ResponseEntity<?> result = controller.getKeywordOccurrences(List.of("test"), 30);
        assertNotNull(result);
    }
    @Test
    void getCategoryStats() {
        when(analyticsService.getArticlesCountByCategory()).thenReturn(Collections.emptyMap());
        ResponseEntity<?> result = controller.getCategoryStats();
        assertNotNull(result);
    }
    @Test
    void getTopKeywords() {
        when(analyticsService.getTopKeywords(anyInt())).thenReturn(Collections.emptyList());
        ResponseEntity<?> result = controller.getTopKeywords(10);
        assertNotNull(result);
    }
    @Test
    void getTopPersonalities() {
        when(analyticsService.getTopPersonalities(anyInt())).thenReturn(Collections.emptyList());
        ResponseEntity<?> result = controller.getTopPersonalities(10);
        assertNotNull(result);
    }
    @Test
    void getAnalytics() {
        AnalyticsResponse response = new AnalyticsResponse();
        when(analyticsService.getAnalytics()).thenReturn(response);
        ResponseEntity<AnalyticsResponse> result = controller.getAnalytics();
        assertNotNull(result);
    }
} 