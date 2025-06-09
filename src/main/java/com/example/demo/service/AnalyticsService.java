package com.example.demo.service;
import com.example.demo.dto.AnalyticsResponse;
import com.example.demo.dto.ArticleFilterRequest;
import com.example.demo.dto.KeywordTrendResponse;
import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;
    @Transactional(readOnly = true)
    public Page<Article> filterArticles(ArticleFilterRequest request) {
        log.info("Filtering articles with request: {}", request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Article> spec = Specification.where(null);
        if (request.getStartDate() != null) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                root.get("category").get("name").in(request.getCategories()));
        }
        if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
            spec = spec.and((root, criteriaQuery, cb) -> {
                var subquery = criteriaQuery.subquery(Long.class);
                var subRoot = subquery.from(Article.class);
                var join = subRoot.join("keywords");
                subquery.select(subRoot.get("id"))
                    .where(cb.and(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        join.get("name").in(request.getKeywords())
                    ));
                return cb.exists(subquery);
            });
        }
        if (request.getSources() != null && !request.getSources().isEmpty()) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                root.get("source").get("name").in(request.getSources()));
        }
        if (request.getSearchQuery() != null && !request.getSearchQuery().trim().isEmpty()) {
            String searchLower = request.getSearchQuery().toLowerCase().trim();
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + searchLower + "%"),
                    cb.like(cb.lower(root.get("content")), "%" + searchLower + "%")
                ));
        }
        return articleRepository.findAll(spec, pageable);
    }
    @Transactional(readOnly = true)
    public Page<Article> searchArticles(String query, ArticleFilterRequest request) {
        log.info("Searching articles with query: {} and filters: {}", query, request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Article> spec = Specification.where(null);
        if (request.getStartDate() != null) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            spec = spec.and((root, criteriaQuery, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
        }
        if (query != null && !query.trim().isEmpty()) {
            String searchLower = query.toLowerCase().trim();
            List<Specification<Article>> searchSpecs = new ArrayList<>();
            if (request.getSearchInTitle()) {
                searchSpecs.add((root, criteriaQuery, cb) -> 
                    cb.like(cb.lower(root.get("title")), "%" + searchLower + "%"));
            }
            if (request.getSearchInContent()) {
                searchSpecs.add((root, criteriaQuery, cb) -> 
                    cb.like(cb.lower(root.get("content")), "%" + searchLower + "%"));
            }
            if (!searchSpecs.isEmpty()) {
                spec = spec.and((root, criteriaQuery, cb) -> {
                    Predicate[] predicates = searchSpecs.stream()
                        .map(searchSpec -> searchSpec.toPredicate(root, criteriaQuery, cb))
                        .toArray(Predicate[]::new);
                    return cb.or(predicates);
                });
            }
        }
        return articleRepository.findAll(spec, pageable);
    }
    @Transactional(readOnly = true)
    public List<Article> getTrendingArticles(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        log.info("Getting trending articles from {} to {} with limit {}", startDate, endDate, limit);
        Specification<Article> spec = Specification.where(null);
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "views"));
        return articleRepository.findAll(spec, pageable).getContent();
    }
    @Transactional(readOnly = true)
    public Map<String, Long> getArticlesCountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting articles count from {} to {}", startDate, endDate);
        Specification<Article> spec = Specification.where(null);
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
        List<Article> articles = articleRepository.findAll(spec);
        return articles.stream()
            .collect(Collectors.groupingBy(
                article -> article.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
    }
    @Transactional(readOnly = true)
    public List<KeywordTrendResponse> getKeywordTrends(LocalDateTime startDate, LocalDateTime endDate, List<String> keywords) {
        log.info("Getting keyword trends for keywords: {} from {} to {}", keywords, startDate, endDate);
        Specification<Article> spec = Specification.where(null);
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
        List<Article> articles = articleRepository.findAll(spec);
        return keywords.stream()
            .map(keyword -> {
                long occurrences = articles.stream()
                    .filter(article -> article.getKeywords().stream()
                        .anyMatch(k -> k.getName().equals(keyword)))
                    .count();
                return new KeywordTrendResponse(keyword, occurrences);
            })
            .collect(Collectors.toList());
    }
    @Cacheable("topKeywords")
    public List<Map.Entry<String, Long>> getTopKeywords(int limit) {
        log.info("Getting top {} keywords", limit);
        try {
            List<Keyword> keywords = keywordRepository.findAll();
            if (keywords == null || keywords.isEmpty()) {
                log.info("No keywords found in the database");
                return Collections.emptyList();
            }
            Map<String, Long> keywordCounts = keywords.stream()
                .filter(keyword -> keyword != null && keyword.getName() != null)
                .collect(Collectors.toMap(
                    Keyword::getName,
                    keyword -> articleRepository.countByKeywordsContaining(keyword),
                    (v1, v2) -> v1,  
                    TreeMap::new  
                ));
            return keywordCounts.entrySet().stream()
                .filter(entry -> entry != null && entry.getKey() != null)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting top keywords: {}", e.getMessage(), e);
            return Collections.emptyList(); 
        }
    }
    @Cacheable("topPersonalities")
    public List<Map.Entry<String, Long>> getTopPersonalities(int limit) {
        log.info("Getting top {} personalities", limit);
        return keywordRepository.findByType(KeywordType.PERSON).stream()
            .collect(Collectors.toMap(
                Keyword::getName,
                keyword -> articleRepository.countByKeywordsContaining(keyword)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public Map<String, Long> getArticlesCountByCategory() {
        log.info("Getting articles count by category");
        return articleRepository.findAll().stream()
            .filter(article -> article.getCategory() != null)
            .collect(Collectors.groupingBy(
                article -> article.getCategory().getName(),
                Collectors.counting()
            ));
    }
    @Transactional(readOnly = true)
    public Map<String, Long> getKeywordOccurrences(List<String> keywords, int days) {
        log.info("Getting keyword occurrences for keywords: {} over {} days", keywords, days);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Map<String, Long> result = new HashMap<>();
        for (String keyword : keywords) {
            long count = articleRepository.findAll().stream()
                .filter(article -> article.getCreatedAt() != null && article.getCreatedAt().isAfter(since))
                .filter(article -> article.getKeywords() != null && article.getKeywords().stream().anyMatch(k -> k.getName().equalsIgnoreCase(keyword)))
                .count();
            result.put(keyword, count);
        }
        return result;
    }
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics() {
        log.info("Getting all analytics data");
        try {
            Map<String, Long> articlesCount = getArticlesCountByDateRange(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            );
            List<Article> trendingArticles = getTrendingArticles(
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now(),
                5
            );
            Map<String, Long> categoryStats = getArticlesCountByCategory();
            List<Map.Entry<String, Long>> topKeywords = getTopKeywords(10);
            AnalyticsResponse response = new AnalyticsResponse();
            response.setTotalArticles(articlesCount.values().stream()
                .mapToLong(Long::longValue)
                .sum());
            response.setCategoryStats(categoryStats.entrySet().stream()
                .map(entry -> {
                    AnalyticsResponse.CategoryStat stat = new AnalyticsResponse.CategoryStat();
                    stat.setName(entry.getKey());
                    stat.setCount(entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList()));
            response.setTopKeywords(topKeywords.stream()
                .map(entry -> {
                    AnalyticsResponse.KeywordStat stat = new AnalyticsResponse.KeywordStat();
                    stat.setId(entry.getKey()); 
                    stat.setName(entry.getKey());
                    stat.setCount(entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList()));
            response.setTrendingArticles(trendingArticles.stream()
                .map(article -> {
                    AnalyticsResponse.TrendingArticle trending = new AnalyticsResponse.TrendingArticle();
                    trending.setId(article.getId());
                    trending.setTitle(article.getTitle());
                    trending.setCategory(article.getCategory().getName());
                    trending.setCreatedAt(article.getCreatedAt().toString());
                    trending.setViews(article.getViews());
                    return trending;
                })
                .collect(Collectors.toList()));
            return response;
        } catch (Exception e) {
            log.error("Error getting analytics data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get analytics data", e);
        }
    }
} 