package com.example.demo.dto.gigachat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {
    @NotNull(message = "Model is required")
    private String model;
    @NotNull(message = "Messages are required")
    @Size(min = 1, message = "At least one message is required")
    private List<GigaChatMessage> messages;
    @JsonProperty("function_call")
    private Object functionCall;
    private List<Function> functions;
    private Boolean stream = false;
    @JsonProperty("update_interval")
    private Integer updateInterval = 0;
    @Min(0)
    @Max(2)
    private Float temperature;
    @Min(0)
    @Max(1)
    @JsonProperty("top_p")
    private Float topP;
    @Min(1)
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @Min(0)
    @JsonProperty("repetition_penalty")
    private Float repetitionPenalty;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Function {
        @NotNull(message = "Function name is required")
        private String name;
        private String description;
        @NotNull(message = "Function parameters are required")
        private Object parameters;
        @JsonProperty("return_parameters")
        private Object returnParameters;
        @JsonProperty("few_shot_examples")
        private List<FunctionExample> fewShotExamples;
        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class FunctionExample {
            @NotNull(message = "Example request is required")
            private String request;
            @NotNull(message = "Example params are required")
            private Object params;
        }
    }
} 