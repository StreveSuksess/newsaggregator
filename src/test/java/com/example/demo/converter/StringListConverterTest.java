package com.example.demo.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringListConverterTest {

    private StringListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringListConverter();
    }

    @Test
    void convertToDatabaseColumn_WithValidList_ShouldReturnJsonString() {
        List<String> attribute = Arrays.asList("item1", "item2", "item3");

        String result = converter.convertToDatabaseColumn(attribute);

        assertNotNull(result);
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
    }

    @Test
    void convertToDatabaseColumn_WithEmptyList_ShouldReturnEmptyJsonArray() {
        List<String> attribute = Collections.emptyList();

        String result = converter.convertToDatabaseColumn(attribute);

        assertEquals("[]", result);
    }

    @Test
    void convertToDatabaseColumn_WithNullList_ShouldReturnEmptyJsonArray() {
        String result = converter.convertToDatabaseColumn(null);

        assertEquals("[]", result);
    }

    @Test
    void convertToDatabaseColumn_WithSingleItemList_ShouldReturnJsonString() {
        List<String> attribute = Collections.singletonList("single-item");

        String result = converter.convertToDatabaseColumn(attribute);

        assertNotNull(result);
        assertTrue(result.contains("single-item"));
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
    }

    @Test
    void convertToEntityAttribute_WithValidJsonString_ShouldReturnList() {
        String dbData = "[\"item1\",\"item2\",\"item3\"]";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
        assertTrue(result.contains("item3"));
    }

    @Test
    void convertToEntityAttribute_WithEmptyJsonArray_ShouldReturnEmptyList() {
        String dbData = "[]";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_WithNullString_ShouldReturnEmptyList() {
        List<String> result = converter.convertToEntityAttribute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_WithEmptyString_ShouldReturnEmptyList() {
        List<String> result = converter.convertToEntityAttribute("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_WithInvalidJson_ShouldReturnEmptyList() {
        String dbData = "invalid json";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_WithSingleItemJson_ShouldReturnSingleItemList() {
        String dbData = "[\"single-item\"]";

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("single-item", result.get(0));
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        List<String> originalList = Arrays.asList("item1", "item2", "item3");

        String dbData = converter.convertToDatabaseColumn(originalList);
        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(originalList.size(), result.size());
        assertTrue(result.containsAll(originalList));
    }

    @Test
    void convertToDatabaseColumn_WithListContainingSpecialCharacters_ShouldHandleCorrectly() {
        List<String> attribute = Arrays.asList("item with spaces", "item\"with\"quotes", "item,with,commas");

        String result = converter.convertToDatabaseColumn(attribute);

        assertNotNull(result);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
        
        // Test that it can be converted back
        List<String> converted = converter.convertToEntityAttribute(result);
        assertEquals(3, converted.size());
    }

    @Test
    void convertToDatabaseColumn_WithListContainingNulls_ShouldHandleCorrectly() {
        List<String> attribute = Arrays.asList("item1", null, "item3");

        String result = converter.convertToDatabaseColumn(attribute);

        assertNotNull(result);
        // Should still be valid JSON
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
    }

    @Test
    void convertToEntityAttribute_WithMalformedJson_ShouldReturnEmptyList() {
        String dbData = "[\"item1\", \"item2\""; // Missing closing bracket

        List<String> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
