package com.example.demo.controller;
import com.example.demo.service.NewsParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/parser")
@RequiredArgsConstructor
@Tag(name = "News Parser", description = "API for managing news parser")
public class NewsParserController {
    private final NewsParserService newsParserService;
    @PostMapping("/run")
    @Operation(summary = "Run parser manually", description = "Starts the news parser manually")
    public ResponseEntity<Void> runParser() {
        newsParserService.parseNews();
        return ResponseEntity.ok().build();
    }
} 