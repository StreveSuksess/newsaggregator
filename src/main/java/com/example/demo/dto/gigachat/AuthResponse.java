package com.example.demo.dto.gigachat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class AuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_at")
    private long expiresAt; 
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt > 1000000000000L ? expiresAt / 1000 : expiresAt;
    }
} 