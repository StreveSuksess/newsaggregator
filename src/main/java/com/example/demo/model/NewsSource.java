package com.example.demo.model;
import com.example.demo.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
@Data
@Entity
@Table(name = "news_source")
public class NewsSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "all_articles_link", nullable = false)
    private String allArticlesLink;
    @Column(name = "article_link_selector")
    private String articleLinkSelector;
    @Column(name = "title_selector")
    private String titleSelector;
    @Column(name = "content_selector")
    private String contentSelector;
    @Column(name = "date_selector")
    private String dateSelector;
    @Column(name = "img_links", columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    private List<String> imgLinks;
} 