package com.example.demo.controller;
import com.example.demo.dto.KeywordDTO;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import com.example.demo.service.KeywordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/keywords")
public class KeywordController {
    private final KeywordService keywordService;
    private static final Logger log = LoggerFactory.getLogger(KeywordController.class);
    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }
    @GetMapping
    public List<KeywordDTO> getAllKeywords() {
        log.info("Getting all keywords");
        return keywordService.findAll().stream()
                .map(KeywordDTO::fromEntity)
                .toList();
    }
    @GetMapping("/{id}")
    public ResponseEntity<KeywordDTO> getById(@PathVariable Long id) {
        return keywordService.findById(id)
                .map(KeywordDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/search")
    public List<KeywordDTO> searchKeywords(@RequestParam String query) {
        log.info("Searching keywords by query: {}", query);
        return keywordService.findByNameContaining(query).stream()
                .map(KeywordDTO::fromEntity)
                .toList();
    }
    @GetMapping("/article/{articleId}")
    public List<KeywordDTO> getByArticleId(@PathVariable Long articleId) {
        return keywordService.findByArticleId(articleId).stream()
            .map(KeywordDTO::fromEntity)
            .collect(Collectors.toList());
    }
    @GetMapping("/by-type")
    public List<KeywordDTO> getKeywordsByType(@RequestParam KeywordType type) {
        log.info("Getting keywords by type: {}", type);
        return keywordService.findByType(type).stream()
                .map(KeywordDTO::fromEntity)
                .toList();
    }
    @PostMapping
    public KeywordDTO create(@RequestBody Keyword keyword) {
        return KeywordDTO.fromEntity(keywordService.save(keyword));
    }
    @PutMapping("/{id}")
    public ResponseEntity<KeywordDTO> update(@PathVariable Long id, @RequestBody Keyword keyword) {
        return keywordService.findById(id)
                .map(existingKeyword -> {
                    existingKeyword.setName(keyword.getName());
                    existingKeyword.setType(keyword.getType());
                    return KeywordDTO.fromEntity(keywordService.save(existingKeyword));
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}/type")
    public ResponseEntity<KeywordDTO> updateKeywordType(
            @PathVariable Long id,
            @RequestParam KeywordType type) {
        try {
            Keyword updatedKeyword = keywordService.updateKeywordType(id, type);
            return ResponseEntity.ok(KeywordDTO.fromEntity(updatedKeyword));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating keyword type: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return keywordService.findById(id)
                .map(keyword -> {
                    keywordService.delete(keyword);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 