package com.example.demo.dto;

import com.example.demo.model.Keyword;
import com.example.demo.model.KeywordType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordDTOTest {

    @Test
    void constructor_ShouldCreateEmptyDTO() {
        KeywordDTO dto = new KeywordDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getType());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        KeywordDTO dto = new KeywordDTO();
        
        dto.setId(1L);
        dto.setName("test-keyword");
        dto.setType(KeywordType.CONCEPT);

        assertEquals(1L, dto.getId());
        assertEquals("test-keyword", dto.getName());
        assertEquals(KeywordType.CONCEPT, dto.getType());
    }

    @Test
    void fromEntity_ShouldConvertKeywordToDTO() {
        Keyword keyword = new Keyword();
        keyword.setId(1L);
        keyword.setName("java");
        keyword.setType(KeywordType.CONCEPT);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("java", dto.getName());
        assertEquals(KeywordType.CONCEPT, dto.getType());
    }

    @Test
    void fromEntity_WithPersonType_ShouldConvertCorrectly() {
        Keyword keyword = new Keyword();
        keyword.setId(2L);
        keyword.setName("John Doe");
        keyword.setType(KeywordType.PERSON);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals(KeywordType.PERSON, dto.getType());
    }

    @Test
    void fromEntity_WithLocationType_ShouldConvertCorrectly() {
        Keyword keyword = new Keyword();
        keyword.setId(3L);
        keyword.setName("Moscow");
        keyword.setType(KeywordType.LOCATION);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("Moscow", dto.getName());
        assertEquals(KeywordType.LOCATION, dto.getType());
    }

    @Test
    void fromEntity_WithOrganizationType_ShouldConvertCorrectly() {
        Keyword keyword = new Keyword();
        keyword.setId(4L);
        keyword.setName("Google");
        keyword.setType(KeywordType.ORGANIZATION);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(4L, dto.getId());
        assertEquals("Google", dto.getName());
        assertEquals(KeywordType.ORGANIZATION, dto.getType());
    }

    @Test
    void fromEntity_WithEventType_ShouldConvertCorrectly() {
        Keyword keyword = new Keyword();
        keyword.setId(5L);
        keyword.setName("World Cup");
        keyword.setType(KeywordType.EVENT);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("World Cup", dto.getName());
        assertEquals(KeywordType.EVENT, dto.getType());
    }

    @Test
    void fromEntity_WithNullType_ShouldHandleGracefully() {
        Keyword keyword = new Keyword();
        keyword.setId(6L);
        keyword.setName("test");
        keyword.setType(null);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(6L, dto.getId());
        assertEquals("test", dto.getName());
        assertNull(dto.getType());
    }

    @Test
    void fromEntity_WithNullName_ShouldHandleGracefully() {
        Keyword keyword = new Keyword();
        keyword.setId(7L);
        keyword.setName(null);
        keyword.setType(KeywordType.CONCEPT);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertEquals(7L, dto.getId());
        assertNull(dto.getName());
        assertEquals(KeywordType.CONCEPT, dto.getType());
    }

    @Test
    void fromEntity_WithAllNullFields_ShouldHandleGracefully() {
        Keyword keyword = new Keyword();
        keyword.setId(null);
        keyword.setName(null);
        keyword.setType(null);

        KeywordDTO dto = KeywordDTO.fromEntity(keyword);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getType());
    }
}
