package com.example.demo.repository;

import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.NewsSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void existsByUrl_WithExistingUrl_ShouldReturnTrue() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        Article article = new Article();
        article.setUrl("http://test.com");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(100L);
        entityManager.persistAndFlush(article);

        // Test
        boolean exists = articleRepository.existsByUrl("http://test.com");

        // Verify
        assertTrue(exists);
    }

    @Test
    void existsByUrl_WithNonExistingUrl_ShouldReturnFalse() {
        boolean exists = articleRepository.existsByUrl("http://nonexistent.com");

        assertFalse(exists);
    }

    @Test
    void findByCreatedAtBefore_WithArticles_ShouldReturnOldArticles() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        LocalDateTime cutoffDate = LocalDateTime.now();
        
        Article oldArticle = new Article();
        oldArticle.setUrl("http://old.com");
        oldArticle.setTitle("Old Article");
        oldArticle.setContent("Old Content");
        oldArticle.setSummary("Old Summary");
        oldArticle.setSource(source);
        oldArticle.setCategory(category);
        oldArticle.setCreatedAt(cutoffDate.minusDays(1));
        oldArticle.setViews(50L);
        entityManager.persistAndFlush(oldArticle);

        Article newArticle = new Article();
        newArticle.setUrl("http://new.com");
        newArticle.setTitle("New Article");
        newArticle.setContent("New Content");
        newArticle.setSummary("New Summary");
        newArticle.setSource(source);
        newArticle.setCategory(category);
        newArticle.setCreatedAt(cutoffDate.plusDays(1));
        newArticle.setViews(150L);
        entityManager.persistAndFlush(newArticle);

        // Test
        List<Article> result = articleRepository.findByCreatedAtBefore(cutoffDate);

        // Verify
        assertEquals(1, result.size());
        assertEquals(oldArticle.getId(), result.get(0).getId());
    }

    @Test
    void findByCreatedAtBefore_WithNoOldArticles_ShouldReturnEmptyList() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
        
        List<Article> result = articleRepository.findByCreatedAtBefore(cutoffDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByCategoryName_WithExistingCategory_ShouldReturnArticles() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        Article article = new Article();
        article.setUrl("http://test.com");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(100L);
        entityManager.persistAndFlush(article);

        // Test
        List<Article> result = articleRepository.findByCategoryName("Technology");

        // Verify
        assertEquals(1, result.size());
        assertEquals(article.getId(), result.get(0).getId());
    }

    @Test
    void findByCategoryName_WithNonExistingCategory_ShouldReturnEmptyList() {
        List<Article> result = articleRepository.findByCategoryName("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void countByCategory_WithArticles_ShouldReturnCount() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        for (int i = 1; i <= 3; i++) {
            Article article = new Article();
            article.setUrl("http://test" + i + ".com");
            article.setTitle("Article " + i);
            article.setContent("Content " + i);
            article.setSummary("Summary " + i);
            article.setSource(source);
            article.setCategory(category);
            article.setCreatedAt(LocalDateTime.now());
            article.setViews((long) i * 10);
            entityManager.persistAndFlush(article);
        }

        // Test
        long count = articleRepository.countByCategory(category);

        // Verify
        assertEquals(3, count);
    }

    @Test
    void findAll_WithPageable_ShouldReturnPagedResults() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        for (int i = 1; i <= 5; i++) {
            Article article = new Article();
            article.setUrl("http://test" + i + ".com");
            article.setTitle("Article " + i);
            article.setContent("Content " + i);
            article.setSummary("Summary " + i);
            article.setSource(source);
            article.setCategory(category);
            article.setCreatedAt(LocalDateTime.now());
            article.setViews((long) i * 10);
            entityManager.persistAndFlush(article);
        }

        // Test
        Pageable pageable = PageRequest.of(0, 3);
        Page<Article> result = articleRepository.findAll(pageable);

        // Verify
        assertEquals(3, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void save_ShouldPersistArticle() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        Article article = new Article();
        article.setUrl("http://test.com");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0L);

        // Test
        Article savedArticle = articleRepository.save(article);

        // Verify
        assertNotNull(savedArticle);
        assertNotNull(savedArticle.getId());
        assertEquals("Test Article", savedArticle.getTitle());
        assertEquals("http://test.com", savedArticle.getUrl());
    }

    @Test
    void findById_WithExistingId_ShouldReturnArticle() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        Article article = new Article();
        article.setUrl("http://test.com");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0L);
        article = entityManager.persistAndFlush(article);

        // Test
        Optional<Article> result = articleRepository.findById(article.getId());

        // Verify
        assertTrue(result.isPresent());
        assertEquals(article.getId(), result.get().getId());
        assertEquals("Test Article", result.get().getTitle());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<Article> result = articleRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_WithExistingId_ShouldDeleteArticle() {
        // Setup
        Category category = new Category();
        category.setName("Technology");
        category = entityManager.persistAndFlush(category);

        NewsSource source = new NewsSource();
        source.setName("Test Source");
        source.setAllArticlesLink("http://test-source.com");
        source = entityManager.persistAndFlush(source);

        Article article = new Article();
        article.setUrl("http://test.com");
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setSummary("Test Summary");
        article.setSource(source);
        article.setCategory(category);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0L);
        article = entityManager.persistAndFlush(article);

        // Test
        articleRepository.deleteById(article.getId());
        entityManager.flush();

        // Verify
        Optional<Article> result = articleRepository.findById(article.getId());
        assertFalse(result.isPresent());
    }
}
