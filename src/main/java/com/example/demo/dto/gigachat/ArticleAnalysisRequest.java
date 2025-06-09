package com.example.demo.dto.gigachat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(description = "Request for article analysis")
public class ArticleAnalysisRequest {
    @Schema(description = "Text of the article to analyze", required = true)
    private String article;
    private String model = "GigaChat";
    private boolean stream = false;
    private int updateInterval = 0;
    private String systemPrompt = """
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
        """;
} 