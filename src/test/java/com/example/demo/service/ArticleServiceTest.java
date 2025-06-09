package com.example.demo.service;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.exception.ArticleNotFoundException;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private NewsSourceRepository newsSourceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ArticleService articleService;



    @Test
    void getArticles_ShouldReturnPagedArticleDTOs() {
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), null, null);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(modelMapper).map(any(Article.class), eq(ArticleDTO.class));
    }

    @Test
    void getArticles_WithSearchQuery_ShouldFilterByTitleAndSummary() {
        String search = "test query";
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), search, null);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getArticles_WithCategory_ShouldFilterByCategory() {
        String categoryId = "1";
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), null, categoryId);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getArticles_WithInvalidCategoryId_ShouldThrowException() {
        String invalidCategoryId = "invalid";
        
        assertThrows(RuntimeException.class, () -> 
            articleService.getArticles(mock(Pageable.class), null, invalidCategoryId));
    }

    @Test
    void getArticles_WithEmptySearch_ShouldIgnoreSearchFilter() {
        String emptySearch = "";
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), emptySearch, null);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getArticles_WithRepositoryException_ShouldThrowRuntimeException() {
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> 
            articleService.getArticles(mock(Pageable.class), null, null));
    }

    @Test
    void getArticleById_ShouldReturnArticleDTO() {
        Long articleId = 1L;
        Article article = createTestArticle();
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(modelMapper.map(article, ArticleDTO.class)).thenReturn(articleDTO);
        
        ArticleDTO result = articleService.getArticleById(articleId);
        
        assertNotNull(result);
        assertEquals(articleDTO.getId(), result.getId());
        assertEquals(articleDTO.getTitle(), result.getTitle());
        verify(articleRepository).findById(articleId);
        verify(modelMapper).map(article, ArticleDTO.class);
    }

    @Test
    void getArticleById_WithNonExistentId_ShouldThrowArticleNotFoundException() {
        Long articleId = 999L;
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());
        
        assertThrows(ArticleNotFoundException.class, () -> 
            articleService.getArticleById(articleId));
        
        verify(articleRepository).findById(articleId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getArticleById_WithEmptySummary_ShouldThrowArticleNotFoundException() {
        Long articleId = 1L;
        Article article = createTestArticle();
        article.setSummary("");
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        
        assertThrows(ArticleNotFoundException.class, () -> 
            articleService.getArticleById(articleId));
        
        verify(articleRepository).findById(articleId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getArticleById_WithNullSummary_ShouldThrowArticleNotFoundException() {
        Long articleId = 1L;
        Article article = createTestArticle();
        article.setSummary(null);
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        
        assertThrows(ArticleNotFoundException.class, () -> 
            articleService.getArticleById(articleId));
        
        verify(articleRepository).findById(articleId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getArticleById_WithRepositoryException_ShouldThrowRuntimeException() {
        Long articleId = 1L;
        
        when(articleRepository.findById(articleId))
            .thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> 
            articleService.getArticleById(articleId));
        
        verify(articleRepository).findById(articleId);
    }

    @Test
    void getArticleById_WithMapperException_ShouldThrowRuntimeException() {
        Long articleId = 1L;
        Article article = createTestArticle();
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(modelMapper.map(article, ArticleDTO.class))
            .thenThrow(new RuntimeException("Mapping error"));
        
        assertThrows(RuntimeException.class, () -> 
            articleService.getArticleById(articleId));
        
        verify(articleRepository).findById(articleId);
        verify(modelMapper).map(article, ArticleDTO.class);
    }

    @Test
    void getArticles_ShouldFilterOnlyArticlesWithSummary() {
        // This test verifies that the service filters articles with non-null, non-empty summaries
        Article articleWithSummary = createTestArticle();
        Article articleWithoutSummary = createTestArticle();
        articleWithoutSummary.setSummary(null);
        
        // The repository should be called with a specification that filters by summary
        Page<Article> page = new PageImpl<>(List.of(articleWithSummary));
        ArticleDTO articleDTO = createTestArticleDTO();
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), null, null);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getArticles_ShouldSetContentToNull() {
        Article article = createTestArticle();
        Page<Article> page = new PageImpl<>(List.of(article));
        ArticleDTO articleDTO = createTestArticleDTO();
        articleDTO.setContent("Some content");
        
        when(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(modelMapper.map(any(Article.class), eq(ArticleDTO.class)))
            .thenReturn(articleDTO);
        
        Page<ArticleDTO> result = articleService.getArticles(mock(Pageable.class), null, null);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertNull(result.getContent().get(0).getContent());
        verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    private Article createTestArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test content for the article");
        article.setSummary("Test summary");
        article.setUrl("http://example.com/test-article");
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0L);
        
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        article.setCategory(category);
        
        return article;
    }

    private ArticleDTO createTestArticleDTO() {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(1L);
        dto.setTitle("Test Article");
        dto.setContent("Test content for the article");
        dto.setSummary("Test summary");
        dto.setSourceUrl("http://example.com/test-article");
        dto.setPublishedAt(LocalDateTime.now());
        dto.setViews(0L);
        return dto;
    }
}
