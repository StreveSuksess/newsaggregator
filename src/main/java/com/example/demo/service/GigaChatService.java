package com.example.demo.service;
import com.example.demo.dto.gigachat.ArticleAnalysisRequest;
import com.example.demo.dto.gigachat.ArticleAnalysisResponse;
import com.example.demo.model.ArticleCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.dto.gigachat.ChatCompletionRequest;
import com.example.demo.dto.gigachat.ChatCompletionResponse;
import com.example.demo.dto.gigachat.GigaChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Collections;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import com.example.demo.config.GigaChatConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.stream.Collectors;
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GigaChatService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GigaChatConfig config;
    @Value("${gigachat.auth-key}")
    private String authKey;
    @Value("${gigachat.base-url}")
    private String baseUrl;
    @Value("${gigachat.auth-url}")
    private String authUrl;
    @Value("${gigachat.scope}")
    private String scope;
    @Value("${gigachat.x-client-id}")
    private String clientId;
    @Value("${gigachat.x-request-id}")
    private String requestId;
    @Value("${gigachat.x-session-id}")
    private String sessionId;
    @Value("${gigachat.client-secret}")
    private String clientSecret;
    private String accessToken;
    private LocalDateTime tokenExpiration;
    private int retryDelay = 1000;
    private static final Logger log = LoggerFactory.getLogger(GigaChatService.class);
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_RETRY_DELAY_MS = 1000;
    private static final int MAX_RETRY_DELAY_MS = 10000;
    public ArticleAnalysisResponse analyzeArticle(String htmlContent) {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                if (accessToken == null) {
                    refreshToken();
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Client-Id", clientId);
                headers.set("X-Request-Id", requestId);
                headers.set("X-Session-Id", sessionId);
                ChatCompletionRequest request = new ChatCompletionRequest();
                request.setModel("GigaChat");
                request.setStream(false);
                request.setTemperature(0.7f);
                GigaChatMessage systemMessage = new GigaChatMessage();
                systemMessage.setRole("system");
                systemMessage.setContent("""
                    Ты - система анализа текста. Твоя задача - проанализировать статью и вернуть JSON в следующем формате:
                    {
                        "summary": "HTML-разметка краткого пересказа статьи с сохранением всех изображений",
                        "keywords": ["ключевое слово 1", "ключевое слово 2", ...],
                        "category": "название категории из списка"
                    }
                    Правила:
                    1. Summary должен быть в формате HTML с сохранением всех изображений из оригинальной статьи
                    2. Summary должен быть кратким (200-300 слов), но информативным
                    3. Keywords должны быть существительными или словосочетаниями
                    4. Category должна быть выбрана из следующего списка категорий в базе данных:
                       - Политика
                       - Экономика
                       - Общество
                       - Наука
                       - Технологии
                       - Спорт
                       - Культура
                       - Происшествия
                       - Здоровье
                       - Образование
                    5. Не выражай собственное мнение
                    6. Используй только факты из текста
                    7. Возвращай только валидный JSON, без дополнительного текста
                    8. При формировании HTML-разметки summary:
                       - Сохраняй все изображения с их оригинальными атрибутами (src, alt, title)
                       - Используй семантические HTML-теги (p, h2, h3, figure, figcaption)
                       - Сохраняй важное форматирование (жирный текст, списки)
                       - Не включай рекламные блоки и навигационные элементы
                    """);
                GigaChatMessage userMessage = new GigaChatMessage();
                userMessage.setRole("user");
                userMessage.setContent("Проанализируй следующую статью и верни результат в указанном формате: " + htmlContent);
                request.setMessages(List.of(systemMessage, userMessage));
                HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
                log.info("Analysis request details:");
                log.info("URL: {}", baseUrl + "/chat/completions");
                log.info("Headers: {}", headers);
                try {
                    ChatCompletionRequest logRequest = new ChatCompletionRequest();
                    logRequest.setModel(request.getModel());
                    logRequest.setStream(request.getStream());
                    logRequest.setTemperature(request.getTemperature());
                    List<GigaChatMessage> logMessages = new ArrayList<>();
                    for (GigaChatMessage msg : request.getMessages()) {
                        GigaChatMessage logMsg = new GigaChatMessage();
                        logMsg.setRole(msg.getRole());
                        if (msg.getRole().equals("user")) {
                            logMsg.setContent("[HTML content omitted]");
                        } else {
                            logMsg.setContent(msg.getContent());
                        }
                        logMessages.add(logMsg);
                    }
                    logRequest.setMessages(logMessages);
                    log.info("Request body (without HTML): {}", objectMapper.writeValueAsString(logRequest));
                } catch (JsonProcessingException e) {
                    log.error("Error serializing request body: {}", e.getMessage());
                }
                log.info("Sending request to GigaChat API (attempt {}/{})", retryCount + 1, MAX_RETRIES);
                ChatCompletionResponse response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
                );
                if (response != null && !response.getChoices().isEmpty()) {
                    String content = response.getChoices().get(0).getMessage().getContent();
                    log.info("Received response from GigaChat (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, content);
                    try {
                        return objectMapper.readValue(content, ArticleAnalysisResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error parsing GigaChat response (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage(), e);
                        throw new RuntimeException("Error parsing GigaChat response", e);
                    }
                }
                throw new RuntimeException("Empty response from GigaChat");
            } catch (Exception e) {
                log.error("Error analyzing article (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (e.getMessage().contains("401") || e.getMessage().contains("429")) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Token expired or rate limit hit, increasing delay to {}ms", currentDelay);
                    accessToken = null; 
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        log.info("Waiting {}ms before retry", currentDelay);
                        Thread.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Article analysis interrupted", ie);
                    }
                    continue;
                }
                throw new RuntimeException("Failed to analyze article after " + MAX_RETRIES + " attempts", e);
            }
        }
        throw new RuntimeException("Failed to analyze article after " + MAX_RETRIES + " attempts");
    }
    private void refreshToken() {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.set("RqUID", UUID.randomUUID().toString());
                headers.set("Authorization", "Basic " + authKey);
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("scope", scope);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
                log.info("Token request details:");
                log.info("URL: {}", authUrl);
                log.info("Headers: {}", headers);
                log.info("Body: {}", body);
                log.info("Requesting new token from GigaChat (attempt {}/{})", retryCount + 1, MAX_RETRIES);
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                    authUrl.trim(),
                    HttpMethod.POST,
                    entity,
                    String.class
                );
                String response = responseEntity.getBody();
                log.info("Token response status: {}", responseEntity.getStatusCode());
                log.info("Token response headers: {}", responseEntity.getHeaders());
                log.info("Token response body: {}", response);
                if (response != null) {
                    try {
                        var jsonNode = objectMapper.readTree(response);
                        if (jsonNode.has("access_token")) {
                            this.accessToken = jsonNode.get("access_token").asText();
                            log.info("Successfully obtained new token");
                            return;
                        } else {
                            log.error("Token response does not contain access_token: {}", response);
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Error parsing token response: {}", e.getMessage(), e);
                    }
                }
                if (responseEntity.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Rate limit hit, increasing delay to {}ms", currentDelay);
                }
            } catch (HttpClientErrorException e) {
                log.error("Error getting token (attempt {}/{}): {} - {}", 
                    retryCount + 1, MAX_RETRIES, 
                    e.getStatusCode(), 
                    e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Rate limit hit, increasing delay to {}ms", currentDelay);
                }
            } catch (Exception e) {
                log.error("Error getting token (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
            }
            retryCount++;
            if (retryCount < MAX_RETRIES) {
                try {
                    log.info("Waiting {}ms before retry", currentDelay);
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Token refresh interrupted", ie);
                }
            }
        }
        throw new RuntimeException("Failed to obtain token after " + MAX_RETRIES + " attempts");
    }
    public ChatCompletionResponse chat(@Valid ChatCompletionRequest request) {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                if (accessToken == null) {
                    refreshToken();
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Client-Id", clientId);
                headers.set("X-Request-Id", requestId);
                headers.set("X-Session-Id", sessionId);
                if (!isValidModel(request.getModel())) {
                    throw new IllegalArgumentException("Invalid model name: " + request.getModel());
                }
                if (request.getFunctionCall() != null) {
                    validateFunctionCall(request);
                }
                HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
                log.info("Chat request details:");
                log.info("URL: {}", baseUrl + "/chat/completions");
                log.info("Headers: {}", headers);
                try {
                    ChatCompletionRequest logRequest = new ChatCompletionRequest();
                    logRequest.setModel(request.getModel());
                    logRequest.setStream(request.getStream());
                    logRequest.setTemperature(request.getTemperature());
                    logRequest.setTopP(request.getTopP());
                    logRequest.setMaxTokens(request.getMaxTokens());
                    logRequest.setRepetitionPenalty(request.getRepetitionPenalty());
                    logRequest.setFunctionCall(request.getFunctionCall());
                    logRequest.setFunctions(request.getFunctions());
                    List<GigaChatMessage> logMessages = new ArrayList<>();
                    for (GigaChatMessage msg : request.getMessages()) {
                        GigaChatMessage logMsg = new GigaChatMessage();
                        logMsg.setRole(msg.getRole());
                        String content = msg.getContent();
                        if (content != null && content.length() > 100) {
                            logMsg.setContent(content.substring(0, 100) + "... [content truncated]");
                        } else {
                            logMsg.setContent(content);
                        }
                        logMsg.setFunctionCall(msg.getFunctionCall());
                        logMsg.setFunctionsStateId(msg.getFunctionsStateId());
                        logMessages.add(logMsg);
                    }
                    logRequest.setMessages(logMessages);
                    log.info("Request body (truncated): {}", objectMapper.writeValueAsString(logRequest));
                } catch (JsonProcessingException e) {
                    log.error("Error serializing request body: {}", e.getMessage());
                }
                log.info("Sending request to GigaChat API (attempt {}/{})", retryCount + 1, MAX_RETRIES);
                ChatCompletionResponse response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
                );
                if (response != null && !response.getChoices().isEmpty()) {
                    log.info("Received response from GigaChat (attempt {}/{}): model={}, finish_reason={}", 
                        retryCount + 1, MAX_RETRIES,
                        response.getModel(),
                        response.getChoices().get(0).getFinishReason());
                    return response;
                }
                throw new RuntimeException("Empty response from GigaChat");
            } catch (Exception e) {
                log.error("Error getting chat completion (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (e.getMessage().contains("401") || e.getMessage().contains("429")) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Token expired or rate limit hit, increasing delay to {}ms", currentDelay);
                    accessToken = null;
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        log.info("Waiting {}ms before retry", currentDelay);
                        Thread.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Chat completion interrupted", ie);
                    }
                    continue;
                }
                throw new RuntimeException("Failed to get chat completion after " + MAX_RETRIES + " attempts", e);
            }
        }
        throw new RuntimeException("Failed to get chat completion after " + MAX_RETRIES + " attempts");
    }
    private boolean isValidModel(String model) {
        return model != null && (
            model.equals("GigaChat") ||
            model.equals("GigaChat-Pro") ||
            model.equals("GigaChat-Max") ||
            model.equals("GigaChat-Pro-preview") ||
            model.equals("GigaChat-Max-preview") ||
            model.equals("Embeddings") ||
            model.equals("EmbeddingsGigaR")
        );
    }
    private void validateFunctionCall(ChatCompletionRequest request) {
        if (request.getFunctionCall() instanceof String) {
            String functionCall = (String) request.getFunctionCall();
            if (!functionCall.equals("auto") && !functionCall.equals("none")) {
                throw new IllegalArgumentException("Invalid function_call value: " + functionCall);
            }
        } else if (request.getFunctionCall() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> functionCall = (Map<String, String>) request.getFunctionCall();
            if (!functionCall.containsKey("name")) {
                throw new IllegalArgumentException("function_call must contain 'name' field");
            }
            String functionName = functionCall.get("name");
            if (request.getFunctions() == null || request.getFunctions().stream()
                    .noneMatch(f -> f.getName().equals(functionName))) {
                throw new IllegalArgumentException("Function '" + functionName + "' is not defined in functions array");
            }
        } else {
            throw new IllegalArgumentException("Invalid function_call format");
        }
    }
    private String sendRequest(String prompt) {
        try {
            var response = analyzeArticle(prompt);
            return response.getSummary();
        } catch (Exception e) {
            log.error("Error sending request to GigaChat: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send request to GigaChat", e);
        }
    }
    public String generateSummary(String content) {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                if (accessToken == null) {
                    refreshToken();
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Client-Id", clientId);
                headers.set("X-Request-Id", requestId);
                headers.set("X-Session-Id", sessionId);
                ChatCompletionRequest request = new ChatCompletionRequest();
                request.setModel("GigaChat");
                request.setStream(false);
                request.setTemperature(0.7f);
                GigaChatMessage systemMessage = new GigaChatMessage();
                systemMessage.setRole("system");
                systemMessage.setContent("""
                    Ты - система генерации краткого содержания. Твоя задача - создать краткое содержание статьи (200-300 слов).
                    Правила:
                    1. Используй только факты из текста
                    2. Не выражай собственное мнение
                    3. Сохраняй важную информацию
                    4. Используй простой и понятный язык
                    """);
                GigaChatMessage userMessage = new GigaChatMessage();
                userMessage.setRole("user");
                userMessage.setContent("Создай краткое содержание следующей статьи: " + content);
                request.setMessages(List.of(systemMessage, userMessage));
                HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
                ChatCompletionResponse response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
                );
                if (response != null && !response.getChoices().isEmpty()) {
                    return response.getChoices().get(0).getMessage().getContent();
                }
                throw new RuntimeException("Empty response from GigaChat");
            } catch (Exception e) {
                log.error("Error generating summary (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (e.getMessage().contains("401") || e.getMessage().contains("429")) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Token expired or rate limit hit, increasing delay to {}ms", currentDelay);
                    accessToken = null;
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        log.info("Waiting {}ms before retry", currentDelay);
                        Thread.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Summary generation interrupted", ie);
                    }
                    continue;
                }
                throw new RuntimeException("Failed to generate summary after " + MAX_RETRIES + " attempts", e);
            }
        }
        throw new RuntimeException("Failed to generate summary after " + MAX_RETRIES + " attempts");
    }
    public List<String> extractKeywords(String text) {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                if (accessToken == null) {
                    refreshToken();
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Client-Id", clientId);
                headers.set("X-Request-Id", requestId);
                headers.set("X-Session-Id", sessionId);
                ChatCompletionRequest request = new ChatCompletionRequest();
                request.setModel("GigaChat");
                request.setStream(false);
                request.setTemperature(0.7f);
                GigaChatMessage systemMessage = new GigaChatMessage();
                systemMessage.setRole("system");
                systemMessage.setContent("""
                    Ты - система извлечения ключевых слов. Твоя задача - извлечь до 5 ключевых слов из текста.
                    Правила:
                    1. Используй только существительные или словосочетания
                    2. Слова должны быть значимыми (не менее 4 букв)
                    3. Возвращай ТОЛЬКО JSON массив строк в формате ["слово1", "слово2", "слово3"]
                    4. Не включай общие слова и местоимения
                    5. Не используй кавычки и другие специальные символы внутри слов
                    6. Не добавляй никакого дополнительного текста или пояснений
                    Пример ответа: ["пляжи", "Анапа", "сезон", "купальщики", "запрет"]
                    """);
                GigaChatMessage userMessage = new GigaChatMessage();
                userMessage.setRole("user");
                userMessage.setContent("Извлеки ключевые слова из следующего текста: " + text);
                request.setMessages(List.of(systemMessage, userMessage));
                HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
                ChatCompletionResponse response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
                );
                if (response != null && !response.getChoices().isEmpty()) {
                    String content = response.getChoices().get(0).getMessage().getContent().trim();
                    try {
                        return objectMapper.readValue(content, List.class);
                    } catch (JsonProcessingException e) {
                        log.warn("Failed to parse keywords as JSON, trying comma-separated format: {}", content);
                        return Arrays.stream(content.split(","))
                            .map(String::trim)
                            .filter(word -> !word.isEmpty())
                            .collect(Collectors.toList());
                    }
                }
                throw new RuntimeException("Empty response from GigaChat");
            } catch (Exception e) {
                log.error("Error extracting keywords (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (e.getMessage().contains("401") || e.getMessage().contains("429")) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Token expired or rate limit hit, increasing delay to {}ms", currentDelay);
                    accessToken = null;
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        log.info("Waiting {}ms before retry", currentDelay);
                        Thread.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Keyword extraction interrupted", ie);
                    }
                    continue;
                }
                throw new RuntimeException("Failed to extract keywords after " + MAX_RETRIES + " attempts", e);
            }
        }
        throw new RuntimeException("Failed to extract keywords after " + MAX_RETRIES + " attempts");
    }
    public String analyzeEntityType(String keyword) {
        int retryCount = 0;
        int currentDelay = INITIAL_RETRY_DELAY_MS;
        while (retryCount < MAX_RETRIES) {
            try {
                if (accessToken == null) {
                    refreshToken();
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);
                headers.set("X-Client-Id", clientId);
                headers.set("X-Request-Id", requestId);
                headers.set("X-Session-Id", sessionId);
                ChatCompletionRequest request = new ChatCompletionRequest();
                request.setModel("GigaChat");
                request.setStream(false);
                request.setTemperature(0.7f);
                GigaChatMessage systemMessage = new GigaChatMessage();
                systemMessage.setRole("system");
                systemMessage.setContent("""
                    Ты - система определения типа сущности. Твоя задача - определить тип сущности из списка:
                    - PERSON (человек)
                    - ORGANIZATION (организация)
                    - LOCATION (место)
                    - EVENT (событие)
                    - CONCEPT (понятие)
                    Правила:
                    1. Возвращай только одно значение из списка
                    2. Используй только заглавные буквы
                    3. Если не уверен, возвращай CONCEPT
                    """);
                GigaChatMessage userMessage = new GigaChatMessage();
                userMessage.setRole("user");
                userMessage.setContent("Определи тип сущности для слова: " + keyword);
                request.setMessages(List.of(systemMessage, userMessage));
                HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
                ChatCompletionResponse response = restTemplate.postForObject(
                    baseUrl + "/chat/completions",
                    entity,
                    ChatCompletionResponse.class
                );
                if (response != null && !response.getChoices().isEmpty()) {
                    return response.getChoices().get(0).getMessage().getContent().trim();
                }
                throw new RuntimeException("Empty response from GigaChat");
            } catch (Exception e) {
                log.error("Error analyzing entity type (attempt {}/{}): {}", retryCount + 1, MAX_RETRIES, e.getMessage());
                if (e.getMessage().contains("401") || e.getMessage().contains("429")) {
                    currentDelay = Math.min(currentDelay * 2, MAX_RETRY_DELAY_MS);
                    log.info("Token expired or rate limit hit, increasing delay to {}ms", currentDelay);
                    accessToken = null;
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        log.info("Waiting {}ms before retry", currentDelay);
                        Thread.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Entity type analysis interrupted", ie);
                    }
                    continue;
                }
                throw new RuntimeException("Failed to analyze entity type after " + MAX_RETRIES + " attempts", e);
            }
        }
        throw new RuntimeException("Failed to analyze entity type after " + MAX_RETRIES + " attempts");
    }
} 