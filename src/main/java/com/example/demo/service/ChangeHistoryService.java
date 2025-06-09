package com.example.demo.service;
import com.example.demo.model.ChangeHistory;
import com.example.demo.repository.ChangeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ChangeHistoryService {
    private final ChangeHistoryRepository changeHistoryRepository;
    private static final int HISTORY_RETENTION_DAYS = 90;
    @Transactional
    public void logArticleCreated(Long articleId, String title) {
        ChangeHistory history = ChangeHistory.builder()
            .type(ChangeHistory.ChangeType.ARTICLE_CREATED)
            .entityType("Article")
            .entityId(articleId)
            .description("Created article: " + title)
            .build();
        changeHistoryRepository.save(history);
    }
    @Transactional
    public void logArticleDeleted(Long articleId, String title) {
        ChangeHistory history = ChangeHistory.builder()
            .type(ChangeHistory.ChangeType.ARTICLE_DELETED)
            .entityType("Article")
            .entityId(articleId)
            .description("Deleted article: " + title)
            .build();
        changeHistoryRepository.save(history);
    }
    @Transactional
    public void logOldArticlesCleaned(int count) {
        ChangeHistory history = ChangeHistory.builder()
            .type(ChangeHistory.ChangeType.OLD_ARTICLES_CLEANED)
            .entityType("Article")
            .entityId(0L)
            .description("Cleaned " + count + " old articles")
            .build();
        changeHistoryRepository.save(history);
    }
    @Transactional(readOnly = true)
    public List<ChangeHistory> getArticleHistory(Long articleId) {
        return changeHistoryRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("Article", articleId);
    }
    @Transactional(readOnly = true)
    public List<ChangeHistory> getChangesByType(ChangeHistory.ChangeType type, LocalDateTime startDate, LocalDateTime endDate) {
        return changeHistoryRepository.findByTypeAndCreatedAtBetweenOrderByCreatedAtDesc(type, startDate, endDate);
    }
    @Transactional
    public void cleanOldHistory() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(HISTORY_RETENTION_DAYS);
        changeHistoryRepository.deleteByCreatedAtBefore(cutoffDate);
    }
} 