package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;
@Data
@Entity
@Table(name = "keyword", schema = "public", uniqueConstraints = {
    @UniqueConstraint(name = "uk_keyword_name", columnNames = {"name"})
})
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private KeywordType type;
    @ManyToMany(mappedBy = "keywords")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Article> articles = new HashSet<>();
} 