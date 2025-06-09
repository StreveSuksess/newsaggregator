package com.example.demo.dto.gigachat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GigaChatMessage {
    private String role; 
    private String content;
    private List<String> attachments;
    @JsonProperty("function_call")
    private FunctionCall functionCall;
    @JsonProperty("functions_state_id")
    private String functionsStateId;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionCall {
        private String name;
        private Object arguments;
    }
} 