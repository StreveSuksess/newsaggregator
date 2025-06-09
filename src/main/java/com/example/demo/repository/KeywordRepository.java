package com.example.demo.repository;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);
    List<Keyword> findByNameContainingIgnoreCase(String name);
    List<Keyword> findAllByNameIn(List<String> names);
    List<Keyword> findByType(KeywordType type);
    @Query("SELECT k FROM Keyword k JOIN k.articles a WHERE a.id = :articleId")
    List<Keyword> findByArticleId(@Param("articleId") Long articleId);
} 