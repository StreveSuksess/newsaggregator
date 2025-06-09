package com.example.demo.controller;
import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.ArticleFilterRequest;
import com.example.demo.dto.KeywordDTO;
import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ArticleNotFoundException;
import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.NewsParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/articles")
@Tag(name = "Articles", description = "API for searching articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final NewsParserService newsParserService;
    private boolean isValidSortField(String field) {
        return List.of("id", "title", "createdAt", "views", "category").contains(field);
    }
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getAllArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (category != null && !category.isEmpty()) {
            try {
                Long categoryId = Long.parseLong(category);
                if (!categoryRepository.existsById(categoryId)) {
                    throw new InvalidRequestException("Category not found with id: " + categoryId);
                }
            } catch (NumberFormatException e) {
                throw new InvalidRequestException("Invalid category ID format");
            }
        }
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length != 2) {
                throw new InvalidRequestException("Invalid sort format. Expected: field,direction");
            }
            String field = sortParams[0].trim();
            String direction = sortParams[1].trim().toUpperCase();
            if (!isValidSortField(field)) {
                throw new InvalidRequestException("Invalid sort field: " + field);
            }
            if (!direction.equals("ASC") && !direction.equals("DESC")) {
                throw new InvalidRequestException("Invalid sort direction. Use ASC or DESC");
            }
            sortObj = Sort.by(Sort.Direction.valueOf(direction), field);
        }
        Specification<Article> spec = (root, query, cb) -> 
            cb.and(
                cb.isNotNull(root.get("summary")),
                cb.notEqual(root.get("summary"), "")
            );
        if (category != null && !category.isEmpty()) {
            Long categoryId = Long.parseLong(category);
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category").get("id"), categoryId)
            );
        }
        if (keyword != null && !keyword.isEmpty()) {
            String keywordLower = keyword.toLowerCase().trim();
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + keywordLower + "%"),
                    cb.like(cb.lower(root.get("summary")), "%" + keywordLower + "%")
                )
            );
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Article> articles = articleRepository.findAll(spec, pageable);
        if (page >= articles.getTotalPages() && articles.getTotalPages() > 0) {
            throw new InvalidRequestException("Page number exceeds total pages");
        }
        Page<ArticleDTO> response = articles.map(ArticleDTO::fromEntity);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search")
    @Operation(summary = "Search articles by keyword", 
               description = "Search articles that contain the specified keyword in their keywords list")
    public ResponseEntity<List<ArticleDTO>> searchByKeyword(
            @Parameter(description = "Keyword to search for") 
            @RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<Article> articles = articleRepository.findByKeywordContaining(keyword.trim());
        List<ArticleDTO> response = articles.stream()
            .map(ArticleDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Search articles by category", 
               description = "Search articles in the specified category. Available categories: Политика, Экономика, Общество, Наука, Технологии, Спорт, Культура, Происшествия, Здоровье, Образование")
    public ResponseEntity<List<ArticleDTO>> searchByCategory(
            @Parameter(description = "Category name to search for") 
            @PathVariable String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new InvalidRequestException("Category name cannot be empty");
        }
        List<Article> articles = articleRepository.findByCategoryName(categoryName.trim());
        List<ArticleDTO> response = articles.stream()
            .map(ArticleDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get article by ID", 
               description = "Returns a single article by its ID")
    public ArticleDTO getArticle(
            @Parameter(description = "Article ID") 
            @PathVariable Long id) {
        log.info("Getting article with ID: {}", id);
        Article article = articleRepository.findByIdWithKeywords(id)
            .orElseThrow(() -> new ArticleNotFoundException("Article not found with id: " + id));
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
        return ArticleDTO.fromEntity(article);
    }
    @PostMapping("/parse")
    @Operation(summary = "Start manual news parsing", 
               description = "Manually triggers the news parsing process for all configured sources")
    public ResponseEntity<Map<String, Object>> startParsing() {
        log.info("Starting manual news parsing");
        try {
            newsParserService.parseNews();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "News parsing started successfully"
            ));
        } catch (Exception e) {
            log.error("Error during manual news parsing: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to start news parsing: " + e.getMessage()
            ));
        }
    }
} 