package com.example.demo.dto;
import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class ArticleAnalysisResponse {
    private String summary;
    private String category;
    private List<String> keywords;
} 