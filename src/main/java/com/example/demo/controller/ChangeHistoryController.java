package com.example.demo.controller;
import com.example.demo.model.ChangeHistory;
import com.example.demo.service.ChangeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ChangeHistoryController {
    private final ChangeHistoryService changeHistoryService;
    @GetMapping("/article/{id}")
    public ResponseEntity<List<ChangeHistory>> getArticleHistory(@PathVariable Long id) {
        return ResponseEntity.ok(changeHistoryService.getArticleHistory(id));
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ChangeHistory>> getChangesByType(
            @PathVariable ChangeHistory.ChangeType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.now().minusDays(7);
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.now();
        return ResponseEntity.ok(changeHistoryService.getChangesByType(type, effectiveStartDate, effectiveEndDate));
    }
} 