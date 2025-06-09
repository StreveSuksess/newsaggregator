package com.example.demo.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;
@Data
public class KeywordTrendsRequest {
    @NotNull(message = "Keywords list cannot be null")
    @NotEmpty(message = "Keywords list cannot be empty")
    @Size(max = 10, message = "Maximum 10 keywords allowed")
    private List<@Size(min = 2, max = 50, message = "Keyword length must be between 2 and 50 characters") String> keywords;
    @NotNull(message = "Days parameter cannot be null")
    @Size(min = 1, max = 365, message = "Days must be between 1 and 365")
    private Integer days = 30; 
} 