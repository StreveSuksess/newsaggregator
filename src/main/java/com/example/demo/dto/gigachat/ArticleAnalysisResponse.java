package com.example.demo.dto.gigachat;
import lombok.Data;
import java.util.List;
@Data
public class ArticleAnalysisResponse {
    private String summary;
    private List<String> keywords;
    private String category;
} 