package com.example.demo.controller;
import com.example.demo.dto.gigachat.ArticleAnalysisRequest;
import com.example.demo.dto.gigachat.ArticleAnalysisResponse;
import com.example.demo.service.GigaChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "Article Analysis", description = "API for analyzing articles using GigaChat")
public class ArticleAnalysisController {
    private final GigaChatService gigaChatService;
    @PostMapping("/article")
    @Operation(
        summary = "Analyze article",
        description = "Analyzes an article and returns keywords and summary using GigaChat AI"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Article successfully analyzed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArticleAnalysisResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error or GigaChat API error"
        )
    })
    public ArticleAnalysisResponse analyzeArticle(@RequestBody ArticleAnalysisRequest request) {
        return gigaChatService.analyzeArticle(request.getArticle());
    }
} 
