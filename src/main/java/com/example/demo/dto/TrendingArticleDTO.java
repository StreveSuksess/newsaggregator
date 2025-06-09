package com.example.demo.dto;
import com.example.demo.model.Article;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class TrendingArticleDTO {
    private Long id;
    private String title;
    private String categoryName;
    private LocalDateTime createdAt;
    private Long views;
    private String summary;
    private String url;
    public static TrendingArticleDTO fromEntity(Article article) {
        TrendingArticleDTO dto = new TrendingArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        dto.setCreatedAt(article.getCreatedAt());
        dto.setViews(article.getViews());
        dto.setSummary(article.getSummary());
        dto.setUrl(article.getUrl());
        return dto;
    }
} 