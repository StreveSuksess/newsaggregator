package com.example.demo.dto;
import lombok.Data;
import java.util.List;
import java.util.Map;
@Data
public class AnalyticsResponse {
    private long totalArticles;
    private List<CategoryStat> categoryStats;
    private List<KeywordStat> topKeywords;
    private List<TrendingArticle> trendingArticles;
    @Data
    public static class CategoryStat {
        private String name;
        private long count;
    }
    @Data
    public static class KeywordStat {
        private String id;
        private String name;
        private long count;
    }
    @Data
    public static class TrendingArticle {
        private long id;
        private String title;
        private String category;
        private String createdAt;
        private long views;
    }
} 