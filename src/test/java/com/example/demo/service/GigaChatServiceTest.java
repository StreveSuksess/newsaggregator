package com.example.demo.service;

import com.example.demo.config.GigaChatConfig;
import com.example.demo.dto.gigachat.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GigaChatServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GigaChatConfig config;

    @InjectMocks
    private GigaChatService gigaChatService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gigaChatService, "authKey", "test-auth-key");
        ReflectionTestUtils.setField(gigaChatService, "baseUrl", "https://gigachat.devices.sberbank.ru/api/v1");
        ReflectionTestUtils.setField(gigaChatService, "authUrl", "https://ngw.devices.sberbank.ru:9443/api/v2/oauth");
        ReflectionTestUtils.setField(gigaChatService, "scope", "GIGACHAT_API_PERS");
        ReflectionTestUtils.setField(gigaChatService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(gigaChatService, "requestId", "test-request-id");
        ReflectionTestUtils.setField(gigaChatService, "sessionId", "test-session-id");
        ReflectionTestUtils.setField(gigaChatService, "accessToken", "test-access-token");
    }

    @Test
    void analyzeArticle_ShouldReturnAnalysis() throws Exception {
        String htmlContent = "<p>Test article content</p>";
        
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        GigaChatMessage message = new GigaChatMessage();
        message.setContent("{\"summary\":\"Test summary\",\"keywords\":[\"test\",\"article\"],\"category\":\"Технологии\"}");
        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        ArticleAnalysisResponse analysisResponse = new ArticleAnalysisResponse();
        analysisResponse.setSummary("Test summary");
        analysisResponse.setKeywords(List.of("test", "article"));
        analysisResponse.setCategory("Технологии");

        when(objectMapper.readValue(anyString(), eq(ArticleAnalysisResponse.class)))
            .thenReturn(analysisResponse);

        ArticleAnalysisResponse result = gigaChatService.analyzeArticle(htmlContent);

        assertNotNull(result);
        assertEquals("Test summary", result.getSummary());
        assertEquals(2, result.getKeywords().size());
        assertEquals("Технологии", result.getCategory());
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void analyzeArticle_WithNullToken_ShouldRefreshToken() throws Exception {
        ReflectionTestUtils.setField(gigaChatService, "accessToken", null);
        
        // Mock token refresh
        ResponseEntity<String> tokenResponse = ResponseEntity.ok("{\"access_token\":\"new-token\"}");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(tokenResponse);
        
        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
        when(jsonNode.has("access_token")).thenReturn(true);
        when(jsonNode.get("access_token")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("new-token");

        // Mock article analysis
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        GigaChatMessage message = new GigaChatMessage();
        message.setContent("{\"summary\":\"Test\",\"keywords\":[],\"category\":\"Технологии\"}");
        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        ArticleAnalysisResponse analysisResponse = new ArticleAnalysisResponse();
        when(objectMapper.readValue(anyString(), eq(ArticleAnalysisResponse.class)))
            .thenReturn(analysisResponse);

        ArticleAnalysisResponse result = gigaChatService.analyzeArticle("test content");

        assertNotNull(result);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void analyzeArticle_WithException_ShouldRetryAndFail() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenThrow(new RestClientException("API error"));

        assertThrows(RuntimeException.class, () -> gigaChatService.analyzeArticle("test content"));
        
        verify(restTemplate, times(3)).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void generateSummary_ShouldReturnSummary() {
        String content = "Test article content";
        
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        GigaChatMessage message = new GigaChatMessage();
        message.setContent("Generated summary");
        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        String result = gigaChatService.generateSummary(content);

        assertEquals("Generated summary", result);
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void generateSummary_WithException_ShouldRetryAndFail() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenThrow(new RestClientException("API error"));

        assertThrows(RuntimeException.class, () -> gigaChatService.generateSummary("test content"));
        
        verify(restTemplate, times(3)).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void extractKeywords_ShouldReturnKeywords() throws Exception {
        String text = "Test article content";
        
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        GigaChatMessage message = new GigaChatMessage();
        message.setContent("[\"keyword1\", \"keyword2\"]");
        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        String[] keywords = {"keyword1", "keyword2"};
        when(objectMapper.readValue(anyString(), eq(String[].class)))
            .thenReturn(keywords);

        List<String> result = gigaChatService.extractKeywords(text);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("keyword1"));
        assertTrue(result.contains("keyword2"));
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void extractKeywords_WithInvalidJson_ShouldReturnEmptyList() throws Exception {
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        GigaChatMessage message = new GigaChatMessage();
        message.setContent("invalid json");
        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        when(objectMapper.readValue(anyString(), eq(String[].class)))
            .thenThrow(new RuntimeException("JSON parsing error"));

        List<String> result = gigaChatService.extractKeywords("test");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void chat_ShouldReturnResponse() {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("GigaChat");
        request.setMessages(List.of(new GigaChatMessage()));
        
        ChatCompletionResponse chatResponse = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        choice.setFinishReason("stop");
        chatResponse.setChoices(List.of(choice));
        chatResponse.setModel("GigaChat");
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenReturn(chatResponse);

        ChatCompletionResponse result = gigaChatService.chat(request);

        assertNotNull(result);
        assertEquals("GigaChat", result.getModel());
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    @Test
    void chat_WithInvalidModel_ShouldThrowException() {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("InvalidModel");
        request.setMessages(List.of(new GigaChatMessage()));

        assertThrows(IllegalArgumentException.class, () -> gigaChatService.chat(request));
    }

    @Test
    void chat_With401Error_ShouldRefreshTokenAndRetry() throws Exception {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("GigaChat");
        request.setMessages(List.of(new GigaChatMessage()));
        
        // First call fails with 401
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class)))
            .thenThrow(new RestClientException("401 Unauthorized"))
            .thenReturn(createMockChatResponse());
        
        // Mock token refresh
        ResponseEntity<String> tokenResponse = ResponseEntity.ok("{\"access_token\":\"new-token\"}");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(tokenResponse);
        
        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
        when(jsonNode.has("access_token")).thenReturn(true);
        when(jsonNode.get("access_token")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("new-token");

        ChatCompletionResponse result = gigaChatService.chat(request);

        assertNotNull(result);
        verify(restTemplate, times(2)).postForObject(anyString(), any(HttpEntity.class), eq(ChatCompletionResponse.class));
    }

    private ChatCompletionResponse createMockChatResponse() {
        ChatCompletionResponse response = new ChatCompletionResponse();
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice();
        choice.setFinishReason("stop");
        response.setChoices(List.of(choice));
        response.setModel("GigaChat");
        return response;
    }
}
