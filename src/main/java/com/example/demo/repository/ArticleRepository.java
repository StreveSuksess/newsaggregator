package com.example.demo.repository;
import com.example.demo.model.Article;
import com.example.demo.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    boolean existsByUrl(String url);
    @Query("SELECT a FROM Article a WHERE a.createdAt < :date")
    List<Article> findByCreatedAtBefore(@Param("date") LocalDateTime date);
    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN FETCH a.source " +
           "LEFT JOIN FETCH a.category c " +
           "WHERE c.name = :categoryName AND " +
           "a.summary IS NOT NULL AND a.summary != ''")
    List<Article> findByCategoryName(@Param("categoryName") String categoryName);
    @Query("SELECT COUNT(a) FROM Article a WHERE a.category = :category")
    long countByCategory(@Param("category") Category category);
    @Query("SELECT DATE_TRUNC('day', a.createdAt) as date, COUNT(a) as count " +
           "FROM Article a " +
           "WHERE a.createdAt >= :startDate " +
           "GROUP BY DATE_TRUNC('day', a.createdAt) " +
           "ORDER BY date")
    List<Map.Entry<LocalDateTime, Long>> findArticleCountByDateRange(@Param("startDate") LocalDateTime startDate);
    @Query("SELECT a FROM Article a WHERE " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) AND " +
           "(:categories IS NULL OR a.category.name IN :categories) AND " +
           "(:sources IS NULL OR a.source.name IN :sources) AND " +
           "(:searchQuery IS NULL OR " +
           "   (a.title LIKE %:searchQuery% OR a.content LIKE %:searchQuery%)) AND " +
           "a.summary IS NOT NULL AND a.summary != ''")
    Page<Article> findArticlesByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("categories") List<String> categories,
            @Param("sources") List<String> sources,
            @Param("searchQuery") String searchQuery,
            Pageable pageable);
    @Query("SELECT a FROM Article a WHERE " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) AND " +
           "(:searchInTitle = true AND a.title LIKE %:query%) OR " +
           "(:searchInContent = true AND a.content LIKE %:query%) AND " +
           "a.summary IS NOT NULL AND a.summary != ''")
    Page<Article> searchArticles(
            @Param("query") String query,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("searchInTitle") boolean searchInTitle,
            @Param("searchInContent") boolean searchInContent,
            Pageable pageable);
    @Query("SELECT a FROM Article a WHERE " +
           "a.createdAt >= :startDate AND a.createdAt <= :endDate AND " +
           "a.summary IS NOT NULL AND a.summary != '' " +
           "ORDER BY a.views DESC")
    List<Article> findTrendingArticles(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.keywords WHERE a.id = :id")
    Optional<Article> findByIdWithKeywords(@Param("id") Long id);
    @Query("SELECT DISTINCT a FROM Article a JOIN a.keywords k WHERE " +
           "LOWER(k.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
           "a.summary IS NOT NULL AND a.summary != ''")
    List<Article> findByKeywordContaining(@Param("keyword") String keyword);
    @Query("SELECT COUNT(a) FROM Article a JOIN a.keywords k WHERE k = :keyword")
    long countByKeywordsContaining(@Param("keyword") com.example.demo.model.Keyword keyword);
} 