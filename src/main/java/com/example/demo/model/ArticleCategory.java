package com.example.demo.model;
public enum ArticleCategory {
    POLITICS("Политика"),
    ECONOMY("Экономика"),
    BUSINESS("Бизнес"),
    FINANCE("Финансы"),
    TECHNOLOGY("Технологии"),
    SCIENCE("Наука"),
    HEALTH("Здоровье"),
    SPORTS("Спорт"),
    CULTURE_AND_ART("Культура и искусство"),
    ENTERTAINMENT("Развлечения и шоу-бизнес"),
    EDUCATION("Образование"),
    SOCIETY("Общество"),
    LAW_AND_SECURITY("Право и безопасность"),
    LIFESTYLE("Советы и лайфстайл"),
    TRAVEL("Путешествия и туризм"),
    ECOLOGY("Экология и окружающая среда"),
    AUTO_AND_TRANSPORT("Авто и транспорт"),
    NATURAL_DISASTERS("Природные катаклизмы"),
    REGIONAL_NEWS("Региональные новости"),
    OPINIONS_AND_ANALYTICS("Мнения и аналитика"),
    CAREER("Карьера и работа"),
    REAL_ESTATE("Недвижимость"),
    RELIGION("Религия и духовность"),
    GAMING("Игры и гейминг"),
    FUTURE_TECHNOLOGY("Технологии будущего / инновации"),
    CRYPTO("Криптовалюты и блокчейн");
    private final String displayName;
    ArticleCategory(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
    public static ArticleCategory fromDisplayName(String displayName) {
        for (ArticleCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + displayName);
    }
} 