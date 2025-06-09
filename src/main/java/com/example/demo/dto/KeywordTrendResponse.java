package com.example.demo.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordTrendResponse {
    private String keyword;
    private Long occurrences;
} 