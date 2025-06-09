package com.example.demo.dto;
import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import lombok.Data;
@Data
public class KeywordDTO {
    private Long id;
    private String name;
    private KeywordType type;
    public static KeywordDTO fromEntity(Keyword keyword) {
        KeywordDTO dto = new KeywordDTO();
        dto.setId(keyword.getId());
        dto.setName(keyword.getName());
        dto.setType(keyword.getType());
        return dto;
    }
} 