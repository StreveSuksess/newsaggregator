package com.example.demo.service;
import com.example.demo.dto.gigachat.ArticleAnalysisResponse;
import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import com.example.demo.model.NewsSource;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.KeywordRepository;
import com.example.demo.repository.NewsSourceRepository;
import com.example.demo.dto.gigachat.ArticleAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class NewsParserService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final GigaChatService gigaChatService;
    private final ChangeHistoryService changeHistoryService;
    private final NewsSourceRepository newsSourceRepository;
    private final KeywordService keywordService;
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(NewsParserService.class);
    @Value("${news.retention.days:3}")
    private int newsRetentionDays;
    @Scheduled(cron = "${news.parser.cron}")
    @Transactional
    public void parseNews() {
        log.info("Starting news parsing from all sources");
        int processed = 0;
        int saved = 0;
        int errors = 0;
        for (NewsSource source : newsSourceRepository.findAll()) {
            try {
                List<Article> articles = fetchArticles(source);
                processed += articles.size();
                for (Article article : articles) {
                    try {
                        Article savedArticle = processArticleInNewTransaction(article, source);
                        if (savedArticle != null) {
                            saved++;
                        }
                    } catch (Exception e) {
                        log.error("Error processing article from source {}: {}", source.getName(), e.getMessage());
                        errors++;
                        continue;
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching articles from source {}: {}", source.getName(), e.getMessage());
                errors++;
                continue;
            }
        }
        log.info("News parsing completed for all sources. Total - Processed: {}, Saved: {}, Errors: {}", 
            processed, saved, errors);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Article processArticleInNewTransaction(Article article, NewsSource source) {
        try {
            article.setSource(source);
            article.setCreatedAt(LocalDateTime.now());
            article.setViews(0L);
            String content = cleanHtmlContent(article.getContent());
            article.setContent(content);
            String summary = generateSummary(content);
            article.setSummary(summary);
            Category category = determineCategory(article.getTitle(), content);
            article.setCategory(category);
            Article savedArticle = articleRepository.save(article);
            try {
                List<String> extractedKeywords = gigaChatService.extractKeywords(content);
                if (extractedKeywords != null && !extractedKeywords.isEmpty()) {
                    Set<Keyword> keywords = keywordService.processKeywords(extractedKeywords);
                    keywordService.saveArticleKeywords(savedArticle, keywords);
                }
            } catch (Exception e) {
                log.error("Error processing keywords for article {}: {}", article.getUrl(), e.getMessage());
            }
            return savedArticle;
        } catch (Exception e) {
            log.error("Error processing article: {}", e.getMessage());
            throw e; 
        }
    }
    private List<Article> fetchArticles(NewsSource source) {
        try {
            Document doc = Jsoup.connect(source.getAllArticlesLink())
                .timeout(10000)
                .get();
            if (doc == null) {
                log.error("Failed to fetch document from source: {}", source.getName());
                return Collections.emptyList();
            }
            return doc.select(source.getArticleLinkSelector())
                .stream()
                .limit(10) 
                .map(articleElement -> {
                    try {
                        String articleUrl = articleElement.attr("href");
                        if (articleUrl == null || articleUrl.isEmpty()) {
                            return null;
                        }
                        if (!articleUrl.startsWith("http")) {
                            articleUrl = new URL(new URL(source.getAllArticlesLink()), articleUrl).toString();
                        }
                        if (articleRepository.existsByUrl(articleUrl)) {
                            return null;
                        }
                        return processArticle(articleElement, source, articleUrl);
                    } catch (Exception e) {
                        log.error("Error processing article from source {}: {}", source.getName(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching news from source {}: {}", source.getName(), e.getMessage());
            return Collections.emptyList();
        }
    }
    private Article processArticle(Element articleElement, NewsSource source, String articleUrl) {
        try {
            Document articleDoc = Jsoup.connect(articleUrl)
                .timeout(10000)
                .get();
            if (articleDoc == null) {
                log.error("Failed to fetch article document from URL: {}", articleUrl);
                return null;
            }
            String title = cleanText(articleDoc.select(source.getTitleSelector()).text());
            String content = cleanHtmlContent(articleDoc.select(source.getContentSelector()).html());
            String summary = generateSummary(content);
            if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
                log.warn("Skipping article with empty title or content: {}", articleUrl);
                return null;
            }
            Article article = new Article();
            article.setUrl(articleUrl);
            article.setTitle(title);
            article.setContent(content);
            article.setSummary(summary);
            article.setSource(source);
            article.setCreatedAt(LocalDateTime.now());
            article.setViews(0L);
            Set<String> links = extractLinks(articleDoc, source);
            article.setImgLinks(new ArrayList<>(links));
            Category category = determineCategory(title, content);
            article.setCategory(category);
            return article;
        } catch (Exception e) {
            log.error("Error processing article from URL {}: {}", articleUrl, e.getMessage());
            return null;
        }
    }
    private Set<String> extractLinks(Document articleDoc, NewsSource source) {
        Set<String> links = new HashSet<>();
        String baseUrl = source.getAllArticlesLink();
        String domain = getDomainFromUrl(baseUrl);

        // Extract image links
        Elements contentImages = articleDoc.select(source.getContentSelector() + " img[src]");
        for (Element img : contentImages) {
            String src = img.attr("src");
            if (isValidUrl(src) && isImageUrl(src)) {
                String absoluteUrl = convertToAbsoluteUrl(src, domain);
                if (absoluteUrl != null) {
                    links.add(absoluteUrl);
                }
            }
        }
        return links;
    }
    private boolean isImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return url.toLowerCase().matches(".*\\.(jpe?g|png|gif|bmp|webp|svg|tiff?|ico)(\\?.*)?$");
    }
    private String getDomainFromUrl(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            log.error("Error extracting domain from URL {}: {}", url, e.getMessage());
            return null;
        }
    }
    private String convertToAbsoluteUrl(String url, String domain) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        url = url.trim();
        
        if (url.startsWith("https://")) {
            return url;
        }

        if (url.startsWith("//")) {
            return "https:" + url;
        }

        if (url.startsWith("/")) {
            return "https://" + domain + url;
        }

        if (!url.startsWith("http")) {
            return "https://" + domain + "/" + url;
        }

        return null;
    }
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("icon")) {
            return false;
        }
        return true;
    }
    String cleanHtmlContent(String content) {
        if (content == null) {
            return "";
        }
        return content.replaceAll("<script[^>]*>.*?</script>", "")
                     .replaceAll("<style[^>]*>.*?</style>", "")
                     .replaceAll("<[^>]*>", " ")
                     .replaceAll("\\s+", " ")
                     .trim();
    }
    private String cleanText(String text) {
        return text != null ? text.trim() : "";
    }
    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        try {
            return gigaChatService.generateSummary(content);
        } catch (Exception e) {
            log.error("Error generating summary: {}", e.getMessage());
            return content.length() > 200 ? content.substring(0, 200) + "..." : content;
        }
    }
    private Category determineCategory(String title, String content) {
        try {
            ArticleAnalysisResponse analysis = gigaChatService.analyzeArticle(title + " " + content);
            if (analysis != null && analysis.getCategory() != null) {
                return categoryRepository.findByName(analysis.getCategory())
                    .orElseGet(() -> {
                        Category category = new Category();
                        category.setName(analysis.getCategory());
                        return categoryRepository.save(category);
                    });
            }
        } catch (Exception e) {
            log.error("Error determining category: {}", e.getMessage());
        }
        return null;
    }
    @Scheduled(cron = "${news.cleanup.cron}")
    @Transactional
    public void cleanOldArticles() {
        log.info("Starting cleanup of old articles");
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            List<Article> oldArticles = articleRepository.findByCreatedAtBefore(cutoffDate);
            if (oldArticles.isEmpty()) {
                log.info("No old articles to clean");
                return;
            }
            for (Article article : oldArticles) {
                try {
                    articleRepository.delete(article);
                } catch (Exception e) {
                    log.error("Error deleting article {}: {}", article.getId(), e.getMessage());
                }
            }
            log.info("Successfully cleaned {} old articles", oldArticles.size());
        } catch (Exception e) {
            log.error("Error during article cleanup: {}", e.getMessage());
        }
    }
} 