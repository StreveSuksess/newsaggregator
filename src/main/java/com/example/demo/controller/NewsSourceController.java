package com.example.demo.controller;
import com.example.demo.model.NewsSource;
import com.example.demo.repository.NewsSourceRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/sources")
public class NewsSourceController {
    private final NewsSourceRepository repo;
    public NewsSourceController(NewsSourceRepository repo) {
        this.repo = repo;
    }
    @GetMapping
    public List<NewsSource> getAll() {
        List<NewsSource> sources = repo.findAll();
        return sources != null ? sources : Collections.emptyList();
    }
    @GetMapping("/names")
    public List<String> getAllNames() {
        List<NewsSource> sources = repo.findAll();
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }
        return sources.stream()
            .filter(source -> source != null && source.getName() != null)
            .map(NewsSource::getName)
            .collect(Collectors.toList());
    }
} 