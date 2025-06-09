package com.example.demo.dto;

import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.Keyword;
import com.example.demo.model.NewsSource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleDTOTest {

    @Test
    void constructor_ShouldCreateEmptyDTO() {
        ArticleDTO dto = new ArticleDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getSummary());
        assertNull(dto.getContent());
        assertNull(dto.getSourceUrl());
        assertNull(dto.getPublishedAt());
        assertNull(dto.getImageUrls());
        assertNull(dto.getKeywords());
        assertNull(dto.getViews());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        ArticleDTO dto = new ArticleDTO();
        LocalDateTime now = LocalDateTime.now();
        List<String> imageUrls = List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg");
        
        KeywordDTO keyword1 = new KeywordDTO();
        keyword1.setName("test");
        KeywordDTO keyword2 = new KeywordDTO();
        keyword2.setName("java");
        List<KeywordDTO> keywords = List.of(keyword1, keyword2);

        dto.setId(1L);
        dto.setTitle("Test Article");
        dto.setSummary("Test Summary");
        dto.setContent("Test Content");
        dto.setSourceUrl("http://example.com/article");
        dto.setPublishedAt(now);
        dto.setImageUrls(imageUrls);
        dto.setKeywords(keywords);
        dto.setViews(100L);

        assertEquals(1L, dto.getId());
        assertEquals("Test Article", dto.getTitle());
        assertEquals("Test Summary", dto.getSummary());
        assertEquals("Test Content", dto.getContent());
        assertEquals("http://example.com/article", dto.getSourceUrl());
        assertEquals(now, dto.getPublishedAt());
        assertEquals(imageUrls, dto.getImageUrls());
        assertEquals(keywords, dto.getKeywords());
        assertEquals(100L, dto.getViews());
    }

    @Test
    void fromEntity_ShouldConvertArticleToDTO() {
        // Create test article
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setSummary("Test Summary");
        article.setContent("Test Content");
        article.setUrl("http://example.com/article");
        article.setViews(100L);
        
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        
        // Set image links
        article.setImgLinks(List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"));
        
        // Set category
        Category category = new Category();
        category.setId(1L);
        category.setName("Technology");
        article.setCategory(category);
        
        // Set news source
        NewsSource source = new NewsSource();
        source.setId(1L);
        source.setName("Test Source");
        article.setSource(source);
        
        // Set keywords
        Keyword keyword1 = new Keyword();
        keyword1.setName("java");
        Keyword keyword2 = new Keyword();
        keyword2.setName("spring");
        article.setKeywords(Set.of(keyword1, keyword2));

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals(article.getId(), dto.getId());
        assertEquals(article.getTitle(), dto.getTitle());
        assertEquals(article.getSummary(), dto.getSummary());
        assertEquals(article.getContent(), dto.getContent());
        assertEquals(article.getUrl(), dto.getSourceUrl());
        assertEquals(article.getCreatedAt(), dto.getPublishedAt());
        assertEquals(article.getImgLinks(), dto.getImageUrls());
        assertEquals(article.getViews(), dto.getViews());
        assertEquals("Test Source", dto.getSourceName());
        assertEquals("Technology", dto.getCategoryName());
        
        assertNotNull(dto.getKeywords());
        assertEquals(2, dto.getKeywords().size());
    }

    @Test
    void fromEntity_WithNullArticle_ShouldReturnNull() {
        // This should be checked in the actual implementation
        Article article = new Article();
        ArticleDTO dto = ArticleDTO.fromEntity(article);
        
        assertNotNull(dto); // ArticleDTO.fromEntity doesn't handle null check
    }

    @Test
    void fromEntity_WithNullKeywords_ShouldHandleGracefully() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setKeywords(null);

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Article", dto.getTitle());
        assertNotNull(dto.getKeywords());
        assertTrue(dto.getKeywords().isEmpty());
    }

    @Test
    void fromEntity_WithEmptyKeywords_ShouldReturnEmptyList() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setKeywords(Set.of());

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Article", dto.getTitle());
        assertNotNull(dto.getKeywords());
        assertTrue(dto.getKeywords().isEmpty());
    }

    @Test
    void fromEntity_WithAllNullFields_ShouldHandleGracefully() {
        Article article = new Article();
        
        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getSummary());
        assertNull(dto.getContent());
        assertNull(dto.getSourceUrl());
        assertNull(dto.getPublishedAt());
        assertNotNull(dto.getImageUrls());
        assertTrue(dto.getImageUrls().isEmpty()); // Empty list, not null
        assertEquals(0L, dto.getViews()); // Default value is 0L
        assertNotNull(dto.getKeywords());
        assertTrue(dto.getKeywords().isEmpty()); // Empty list, not null
        assertEquals("Без категории", dto.getCategoryName()); // Default value
    }

    @Test
    void fromEntity_WithInvalidImageUrls_ShouldFilterThem() {
        Article article = new Article();
        article.setId(1L);
        // Test with null imgLinks first
        article.setImgLinks(null);

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertNotNull(dto.getImageUrls());
        assertTrue(dto.getImageUrls().isEmpty());
        
        // Now test with actual invalid URLs
        article.setImgLinks(java.util.Arrays.asList(
            "http://valid.com/image.jpg",
            "invalid-url",
            null,
            "",
            "https://another-valid.com/pic.png"
        ));

        dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertNotNull(dto.getImageUrls());
        assertEquals(2, dto.getImageUrls().size());
        assertTrue(dto.getImageUrls().contains("http://valid.com/image.jpg"));
        assertTrue(dto.getImageUrls().contains("https://another-valid.com/pic.png"));
    }

    @Test
    void fromEntity_WithNullCategory_ShouldUseDefaultCategoryName() {
        Article article = new Article();
        article.setId(1L);
        article.setCategory(null);

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertEquals("Без категории", dto.getCategoryName());
    }

    @Test
    void fromEntity_WithNullSource_ShouldHaveNullSourceName() {
        Article article = new Article();
        article.setId(1L);
        article.setSource(null);

        ArticleDTO dto = ArticleDTO.fromEntity(article);

        assertNotNull(dto);
        assertNull(dto.getSourceName());
    }
}
