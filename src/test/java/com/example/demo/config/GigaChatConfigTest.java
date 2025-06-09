package com.example.demo.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GigaChatConfigTest {

    @Test
    void constructor_ShouldCreateConfigWithDefaultValues() {
        GigaChatConfig config = new GigaChatConfig();
        
        assertNotNull(config);
        assertNull(config.getAuthKey());
        assertEquals("https://gigachat.devices.sberbank.ru/api/v1", config.getBaseUrl());
        assertEquals("https://ngw.devices.sberbank.ru:9443/api/v2/oauth", config.getAuthUrl());
        assertEquals("GIGACHAT_API_PERS", config.getScope());
        assertNull(config.getXClientId());
        assertNull(config.getXRequestId());
        assertNull(config.getXSessionId());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setAuthKey("test-auth-key");
        config.setBaseUrl("https://custom-base-url.com");
        config.setAuthUrl("https://custom-auth-url.com");
        config.setScope("CUSTOM_SCOPE");
        config.setXClientId("test-client-id");
        config.setXRequestId("test-request-id");
        config.setXSessionId("test-session-id");

        assertEquals("test-auth-key", config.getAuthKey());
        assertEquals("https://custom-base-url.com", config.getBaseUrl());
        assertEquals("https://custom-auth-url.com", config.getAuthUrl());
        assertEquals("CUSTOM_SCOPE", config.getScope());
        assertEquals("test-client-id", config.getXClientId());
        assertEquals("test-request-id", config.getXRequestId());
        assertEquals("test-session-id", config.getXSessionId());
    }

    @Test
    void setAuthKey_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setAuthKey(null);
        
        assertNull(config.getAuthKey());
    }

    @Test
    void setBaseUrl_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setBaseUrl(null);
        
        assertNull(config.getBaseUrl());
    }

    @Test
    void setAuthUrl_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setAuthUrl(null);
        
        assertNull(config.getAuthUrl());
    }

    @Test
    void setScope_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setScope(null);
        
        assertNull(config.getScope());
    }

    @Test
    void setXClientId_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setXClientId(null);
        
        assertNull(config.getXClientId());
    }

    @Test
    void setXRequestId_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setXRequestId(null);
        
        assertNull(config.getXRequestId());
    }

    @Test
    void setXSessionId_WithNullValue_ShouldAcceptNull() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setXSessionId(null);
        
        assertNull(config.getXSessionId());
    }

    @Test
    void setBaseUrl_WithEmptyString_ShouldAcceptEmptyString() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setBaseUrl("");
        
        assertEquals("", config.getBaseUrl());
    }

    @Test
    void setAuthUrl_WithEmptyString_ShouldAcceptEmptyString() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setAuthUrl("");
        
        assertEquals("", config.getAuthUrl());
    }

    @Test
    void setScope_WithEmptyString_ShouldAcceptEmptyString() {
        GigaChatConfig config = new GigaChatConfig();
        
        config.setScope("");
        
        assertEquals("", config.getScope());
    }

    @Test
    void setAuthKey_WithLongValue_ShouldAcceptLongValue() {
        GigaChatConfig config = new GigaChatConfig();
        String longAuthKey = "a".repeat(1000);
        
        config.setAuthKey(longAuthKey);
        
        assertEquals(longAuthKey, config.getAuthKey());
    }

    @Test
    void setXClientId_WithUUIDFormat_ShouldAcceptUUID() {
        GigaChatConfig config = new GigaChatConfig();
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        
        config.setXClientId(uuid);
        
        assertEquals(uuid, config.getXClientId());
    }

    @Test
    void setXRequestId_WithUUIDFormat_ShouldAcceptUUID() {
        GigaChatConfig config = new GigaChatConfig();
        String uuid = "987fcdeb-51d2-4321-9876-543210987654";
        
        config.setXRequestId(uuid);
        
        assertEquals(uuid, config.getXRequestId());
    }

    @Test
    void setXSessionId_WithUUIDFormat_ShouldAcceptUUID() {
        GigaChatConfig config = new GigaChatConfig();
        String uuid = "abcdef12-3456-7890-abcd-ef1234567890";
        
        config.setXSessionId(uuid);
        
        assertEquals(uuid, config.getXSessionId());
    }
}
