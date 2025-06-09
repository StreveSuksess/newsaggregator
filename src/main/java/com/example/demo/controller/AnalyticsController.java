package com.example.demo.controller;
import com.example.demo.dto.AnalyticsResponse;
import com.example.demo.dto.ArticleFilterRequest;
import com.example.demo.dto.KeywordTrendResponse;
import com.example.demo.model.Article;
import com.example.demo.service.AnalyticsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    @GetMapping("/articles/filter")
    public ResponseEntity<?> filterArticles(@Valid ArticleFilterRequest request) {
        log.info("Received filter articles request: {}", request);
        return ResponseEntity.ok(analyticsService.filterArticles(request));
    }
    @GetMapping("/articles/search")
    public ResponseEntity<?> searchArticles(
            @RequestParam String query,
            @Valid ArticleFilterRequest request) {
        log.info("Received search articles request with query: {} and filters: {}", query, request);
        return ResponseEntity.ok(analyticsService.searchArticles(query, request));
    }
    @GetMapping("/articles/trending")
    public ResponseEntity<?> getTrendingArticles(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        log.info("Getting trending articles from {} to {} with limit {}", startDate, endDate, limit);
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok(analyticsService.getTrendingArticles(startDate, endDate, limit));
    }
    @GetMapping("/articles/count")
    public ResponseEntity<?> getArticlesCount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting articles count from {} to {}", startDate, endDate);
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok(analyticsService.getArticlesCountByDateRange(startDate, endDate));
    }
    @GetMapping("/keywords/trends")
    public ResponseEntity<?> getKeywordTrends(
            @RequestParam @Size(min = 1, max = 10) List<String> keywords,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting keyword trends for keywords: {} from {} to {}", keywords, startDate, endDate);
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        return ResponseEntity.ok(analyticsService.getKeywordTrends(startDate, endDate, keywords));
    }
    @GetMapping("/keywords/occurrences")
    public ResponseEntity<?> getKeywordOccurrences(
            @RequestParam @Size(min = 1, max = 10) List<String> keywords,
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int days) {
        log.info("Getting keyword occurrences for keywords: {} over {} days", keywords, days);
        return ResponseEntity.ok(analyticsService.getKeywordOccurrences(keywords, days));
    }
    @GetMapping("/categories/stats")
    public ResponseEntity<?> getCategoryStats() {
        log.info("Getting category statistics");
        return ResponseEntity.ok(analyticsService.getArticlesCountByCategory());
    }
    @GetMapping("/keywords/top")
    public ResponseEntity<?> getTopKeywords(
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        log.info("Getting top {} keywords", limit);
        return ResponseEntity.ok(analyticsService.getTopKeywords(limit));
    }
    @GetMapping("/personalities/top")
    public ResponseEntity<?> getTopPersonalities(
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        log.info("Getting top {} personalities", limit);
        return ResponseEntity.ok(analyticsService.getTopPersonalities(limit));
    }
    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        log.info("Getting all analytics data");
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}