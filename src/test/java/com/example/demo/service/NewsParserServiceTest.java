package com.example.demo.service;

import com.example.demo.dto.gigachat.ArticleAnalysisResponse;
import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.Keyword;
import com.example.demo.model.NewsSource;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.KeywordRepository;
import com.example.demo.repository.NewsSourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsParserServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GigaChatService gigaChatService;

    @Mock
    private ChangeHistoryService changeHistoryService;

    @Mock
    private NewsSourceRepository newsSourceRepository;

    @Mock
    private KeywordService keywordService;

    @InjectMocks
    private NewsParserService newsParserService;

    @Test
    void parseNews_ShouldProcessAllSources() {
        NewsSource source = createTestNewsSource();
        when(newsSourceRepository.findAll()).thenReturn(List.of(source));

        newsParserService.parseNews();

        verify(newsSourceRepository).findAll();
    }

    @Test
    void parseNews_WithException_ShouldContinueWithOtherSources() {
        NewsSource source1 = createTestNewsSource();
        source1.setName("Source1");
        NewsSource source2 = createTestNewsSource();
        source2.setName("Source2");
        
        when(newsSourceRepository.findAll()).thenReturn(List.of(source1, source2));

        newsParserService.parseNews();

        verify(newsSourceRepository).findAll();
    }

    @Test
    void cleanHtmlContent_ShouldRemoveHtmlTags() {
        String content = "<script>alert('test')</script><p>Hello <b>World</b></p><style>body{}</style>";
        
        String result = newsParserService.cleanHtmlContent(content);
        
        assertNotNull(result);
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("<style>"));
        assertFalse(result.contains("<p>"));
        assertFalse(result.contains("<b>"));
    }

    @Test
    void cleanHtmlContent_WithNullInput_ShouldReturnEmptyString() {
        String result = newsParserService.cleanHtmlContent(null);
        
        assertEquals("", result);
    }

    @Test
    void cleanHtmlContent_WithEmptyInput_ShouldReturnEmptyString() {
        String result = newsParserService.cleanHtmlContent("");
        
        assertEquals("", result);
    }

    @Test
    void cleanHtmlContent_WithPlainText_ShouldReturnSameText() {
        String plainText = "This is plain text without HTML";
        
        String result = newsParserService.cleanHtmlContent(plainText);
        
        assertEquals(plainText.trim(), result);
    }

    @Test
    void cleanOldArticles_ShouldDeleteOldArticles() {
        Article oldArticle = createTestArticle();
        oldArticle.setCreatedAt(LocalDateTime.now().minusDays(40));
        
        when(articleRepository.findByCreatedAtBefore(any(LocalDateTime.class)))
            .thenReturn(List.of(oldArticle));

        newsParserService.cleanOldArticles();

        verify(articleRepository).findByCreatedAtBefore(any(LocalDateTime.class));
        verify(articleRepository).deleteAll(List.of(oldArticle));
    }

    @Test
    void cleanOldArticles_WithNoOldArticles_ShouldNotDeleteAnything() {
        when(articleRepository.findByCreatedAtBefore(any(LocalDateTime.class)))
            .thenReturn(List.of());

        newsParserService.cleanOldArticles();

        verify(articleRepository).findByCreatedAtBefore(any(LocalDateTime.class));
        verify(articleRepository, never()).deleteAll(anyList());
    }

    @Test
    void cleanOldArticles_WithException_ShouldHandleGracefully() {
        when(articleRepository.findByCreatedAtBefore(any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> newsParserService.cleanOldArticles());

        verify(articleRepository).findByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void cleanOldArticles_WithDeleteException_ShouldContinueWithOtherArticles() {
        Article article1 = createTestArticle();
        article1.setId(1L);
        Article article2 = createTestArticle();
        article2.setId(2L);
        
        when(articleRepository.findByCreatedAtBefore(any(LocalDateTime.class)))
            .thenReturn(List.of(article1, article2));
        doThrow(new RuntimeException("Delete error")).when(articleRepository).deleteAll(anyList());

        newsParserService.cleanOldArticles();

        verify(articleRepository).deleteAll(anyList());
    }

    private NewsSource createTestNewsSource() {
        NewsSource source = new NewsSource();
        source.setId(1L);
        source.setName("Test Source");
        source.setAllArticlesLink("https://test.com/articles");
        source.setArticleLinkSelector("a.article");
        source.setTitleSelector("h1.title");
        source.setContentSelector("div.content");
        return source;
    }

    private Article createTestArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test content");
        article.setSummary("Test summary");
        article.setUrl("http://test.com");
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0L);
        return article;
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        return category;
    }
}
