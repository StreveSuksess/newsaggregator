package com.example.demo.repository;
import com.example.demo.model.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Long> {
    List<ChangeHistory> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    List<ChangeHistory> findByTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            ChangeHistory.ChangeType type, 
            LocalDateTime startDate, 
            LocalDateTime endDate);
    void deleteByCreatedAtBefore(LocalDateTime date);
} 