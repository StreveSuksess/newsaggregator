package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "article_history")
public class ArticleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    @Column(nullable = false)
    private String action;
    @Column(name = "performed_at")
    private LocalDateTime performedAt;
    @Column(columnDefinition = "jsonb")
    private String details;
} 