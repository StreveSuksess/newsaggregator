package com.example.demo.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.exception.ArticleNotFoundException;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.dto.ArticleDTO;
@Service
public class ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }
    @Transactional(readOnly = true)
    public Page<ArticleDTO> getArticles(Pageable pageable, String search, String category) {
        try {
            Specification<Article> spec = Specification.where(null);
            spec = spec.and((root, query, cb) -> 
                cb.and(
                    cb.isNotNull(root.get("summary")),
                    cb.notEqual(root.get("summary"), "")
                )
            );
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase().trim();
                spec = spec.and((root, query, cb) -> 
                    cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + searchLower + "%"),
                        cb.like(cb.lower(root.get("summary")), "%" + searchLower + "%")
                    )
                );
            }
            if (category != null && !category.trim().isEmpty()) {
                try {
                    Long categoryId = Long.parseLong(category.trim());
                    spec = spec.and((root, query, cb) -> 
                        cb.equal(root.get("category").get("id"), categoryId)
                    );
                } catch (NumberFormatException e) {
                    throw new InvalidRequestException("Invalid category ID format");
                }
            }
            Page<Article> articles = articleRepository.findAll(spec, pageable);
            if (articles.isEmpty() && (search != null || category != null)) {
                logger.info("No articles found for search: '{}', category: '{}'", search, category);
            }
            return articles.map(article -> {
                ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);
                dto.setContent(null); 
                return dto;
            });
        } catch (Exception e) {
            logger.error("Error fetching articles: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch articles", e);
        }
    }
    @Transactional(readOnly = true)
    public ArticleDTO getArticleById(Long id) {
        try {
            Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found with id: " + id));
            if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
                throw new ArticleNotFoundException("Article summary is empty for id: " + id);
            }
            return modelMapper.map(article, ArticleDTO.class);
        } catch (ArticleNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching article by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch article", e);
        }
    }
} 