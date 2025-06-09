package com.example.demo.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "gigachat")
public class GigaChatConfig {
    private String authKey;
    private String baseUrl = "https://gigachat.devices.sberbank.ru/api/v1";
    private String authUrl = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    private String scope = "GIGACHAT_API_PERS";
    private String xClientId;
    private String xRequestId;
    private String xSessionId;
    public String getAuthKey() {
        return authKey;
    }
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getAuthUrl() {
        return authUrl;
    }
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getXClientId() {
        return xClientId;
    }
    public void setXClientId(String xClientId) {
        this.xClientId = xClientId;
    }
    public String getXRequestId() {
        return xRequestId;
    }
    public void setXRequestId(String xRequestId) {
        this.xRequestId = xRequestId;
    }
    public String getXSessionId() {
        return xSessionId;
    }
    public void setXSessionId(String xSessionId) {
        this.xSessionId = xSessionId;
    }
} 