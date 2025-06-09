package com.example.demo.service;
import com.example.demo.model.Article;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import com.example.demo.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    /**
     * Process and save keywords for an article
     * @param keywordNames List of keyword names to process
     * @return Set of processed and saved keywords
     */
    @Transactional
    public Set<Keyword> processKeywords(List<String> keywordNames) {
        if (keywordNames == null || keywordNames.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> normalizedKeywords = keywordNames.stream()
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .map(String::toLowerCase)
            .distinct()
            .collect(Collectors.toList());
        if (normalizedKeywords.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Keyword> result = new HashSet<>();
        for (String name : normalizedKeywords) {
            Optional<Keyword> existingKeyword = keywordRepository.findByName(name);
            if (existingKeyword.isPresent()) {
                result.add(existingKeyword.get());
            } else {
                Keyword keyword = new Keyword();
                keyword.setName(name);
                keyword.setType(KeywordType.CONCEPT);
                result.add(keywordRepository.save(keyword));
            }
        }
        return result;
    }
    /**
     * Update keyword type
     * @param keywordId ID of the keyword to update
     * @param type New type for the keyword
     * @return Updated keyword
     */
    @Transactional
    public Keyword updateKeywordType(Long keywordId, KeywordType type) {
        Keyword keyword = keywordRepository.findById(keywordId)
            .orElseThrow(() -> new IllegalArgumentException("Keyword not found with id: " + keywordId));
        keyword.setType(type);
        return keywordRepository.save(keyword);
    }
    /**
     * Find keywords by name (case-insensitive partial match)
     * @param name Keyword name to search for
     * @return List of matching keywords
     */
    @Transactional(readOnly = true)
    public List<Keyword> findByNameContaining(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        return keywordRepository.findByNameContainingIgnoreCase(name.trim());
    }
    /**
     * Get all keywords
     * @return List of all keywords
     */
    @Transactional(readOnly = true)
    public List<Keyword> findAll() {
        return keywordRepository.findAll();
    }
    /**
     * Find a keyword by its ID
     * @param id Keyword ID
     * @return Optional containing the keyword if found
     */
    @Transactional(readOnly = true)
    public Optional<Keyword> findById(Long id) {
        return keywordRepository.findById(id);
    }
    /**
     * Find keywords by article ID
     * @param articleId Article ID
     * @return List of keywords associated with the article
     */
    @Transactional(readOnly = true)
    public List<Keyword> findByArticleId(Long articleId) {
        return keywordRepository.findByArticleId(articleId);
    }
    /**
     * Find keywords by type
     * @param type Keyword type to search for
     * @return List of keywords of the specified type
     */
    @Transactional(readOnly = true)
    public List<Keyword> findByType(KeywordType type) {
        return keywordRepository.findByType(type);
    }
    /**
     * Save a single keyword
     * @param keyword Keyword to save
     * @return Saved keyword
     */
    @Transactional
    public Keyword save(Keyword keyword) {
        return keywordRepository.save(keyword);
    }
    /**
     * Delete a keyword
     * @param keyword Keyword to delete
     */
    @Transactional
    public void delete(Keyword keyword) {
        keywordRepository.delete(keyword);
    }
    /**
     * Save article keywords
     * @param article Article to save keywords for
     * @param keywords Set of keywords to save
     */
    @Transactional
    public void saveArticleKeywords(Article article, Set<Keyword> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
        article.getKeywords().addAll(keywords);
    }
} 