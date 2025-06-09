package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@Entity
@Table(name = "article", schema = "public")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "url", nullable = false, unique = true, length = 2048)
    private String url;
    @Column(name = "title", nullable = false, length = 1024)
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private NewsSource source;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "img_links", columnDefinition = "TEXT[]")
    private List<String> imgLinks;
    @Column(name = "views", nullable = false)
    private Long views = 0L;
    @ManyToMany
    @JoinTable(
        name = "article_keyword",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();
    public Set<Keyword> getKeywords() {
        return keywords;
    }
    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }
} 