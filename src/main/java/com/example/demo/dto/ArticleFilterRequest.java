package com.example.demo.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class ArticleFilterRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    @Size(max = 10)
    private List<String> categories;
    @Size(max = 10)
    private List<String> keywords;
    @Size(max = 10)
    private List<String> sources;
    private String searchQuery;
    @Min(0)
    private Integer page = 0;
    @Min(1)
    private Integer size = 20;
    private Boolean searchInTitle = true;
    private Boolean searchInContent = true;
} 