package com.example.demo.dto;
import com.example.demo.model.Article;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.net.URL;
import java.net.MalformedURLException;
@Data
public class ArticleDTO {
    private Long id;
    private String url;
    private String sourceUrl;
    private String title;
    private String content;
    private String summary;
    private String sourceName;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private List<String> imageUrls;
    private List<KeywordDTO> keywords;
    private Long views;
    public static ArticleDTO fromEntity(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setUrl(article.getUrl());
        dto.setSourceUrl(article.getUrl()); 
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setSourceName(article.getSource() != null ? article.getSource().getName() : null);
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : "Без категории");
        dto.setCreatedAt(article.getCreatedAt());
        dto.setPublishedAt(article.getCreatedAt()); 
        dto.setViews(article.getViews() != null ? article.getViews() : 0L);
        List<String> validImageUrls = Collections.emptyList();
        if (article.getImgLinks() != null) {
            validImageUrls = article.getImgLinks().stream()
                .filter(ArticleDTO::isValidImageUrl)
                .collect(Collectors.toList());
        }
        dto.setImageUrls(validImageUrls);
        List<KeywordDTO> keywordDTOs = Collections.emptyList();
        if (article.getKeywords() != null) {
            keywordDTOs = article.getKeywords().stream()
                .filter(keyword -> keyword != null)
                .map(KeywordDTO::fromEntity)
                .collect(Collectors.toList());
        }
        dto.setKeywords(keywordDTOs);
        return dto;
    }
    private static boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        try {
            new URL(url);
            return url.toLowerCase().matches(".*\\.(jpe?g|png|gif|bmp|webp|svg|tiff?|ico)(\\?.*)?$");
        } catch (MalformedURLException e) {
            return false;
        }
    }
} 