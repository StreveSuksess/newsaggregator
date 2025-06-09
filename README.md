# News Aggregator

Новостной портал с анализом и обработкой новостных статей.

## Архитектура

- **Backend**: Spring Boot + PostgreSQL
- **Frontend**: React + TypeScript
- **Deployment**: Docker Compose

## Быстрый старт

1. Запустить проект:
```bash
docker-compose up -d
```

2. Открыть в браузере:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Добавление нового источника новостей

Для добавления нового источника новостей необходимо выполнить следующие шаги:

1. Добавить запись в таблицу `news_source` через SQL:
```sql
INSERT INTO news_source (
    name,
    all_articles_link,
    article_link_selector,
    title_selector,
    content_selector,
    date_selector,
    img_links
) VALUES (
    'Название источника',                    -- например: 'РИА Новости'
    'https://ria.ru/',                      -- URL главной страницы
    'a.article-item__link',                 -- CSS селектор для ссылок на статьи
    'h1.article__title',                    -- CSS селектор для заголовка статьи
    'div.article__block',                   -- CSS селектор для контента статьи
    'div.article__info-date',               -- CSS селектор для даты публикации
    '[]'::jsonb                             -- Пустой массив для селекторов изображений
);
```

2. Проверить селекторы:
   - Откройте страницу источника в браузере
   - Используйте инструменты разработчика (F12)
   - Проверьте, что селекторы корректно выбирают нужные элементы
   - Для проверки можно использовать консоль браузера:
     ```javascript
     document.querySelector('селектор')  // должен вернуть элемент
     ```

3. Проверить работу парсера:
   - Запустите парсинг вручную через API:
     ```bash
     curl -X POST http://localhost:8080/api/sources/parse
     ```
   - Проверьте логи на наличие ошибок
   - Убедитесь, что статьи успешно сохраняются в базу

4. Настройка селекторов изображений:
   - Если нужно извлечь изображения из определенных элементов, добавьте селекторы в `img_links`:
     ```sql
     UPDATE news_source 
     SET img_links = '["img.article__image", "div.gallery img"]'::jsonb 
     WHERE name = 'Название источника';
     ```

## Структура проекта

```
JavaProject/
├── src/main/java/           # Backend Java код
├── src/main/resources/      # Конфигурация и ресурсы
├── src/test/java/           # Тесты (100% покрытие бизнес-логики критически важных методов)
├── frontend/src/            # Frontend React код
├── docker-compose.yml      # Docker конфигурация
├── Dockerfile              # Backend Docker образ
└── frontend/Dockerfile     # Frontend Docker образ
```

## API Endpoints

### Статьи
- `GET /api/articles` - Список статей с пагинацией
- `GET /api/articles/{id}` - Получить статью по ID
- `GET /api/articles/search` - Поиск по ключевым словам
- `GET /api/articles/category/{name}` - Статьи по категории

### Аналитика
- `GET /api/analytics` - Общая аналитика
- `GET /api/analytics/categories/stats` - Статистика по категориям
- `GET /api/analytics/keywords/top` - Топ ключевых слов
- `GET /api/analytics/keywords/trends` - Тренды ключевых слов

### Источники
- `GET /api/sources` - Список источников новостей
- `GET /api/sources/names` - Названия источников

### Категории
- `GET /api/categories` - Список категорий

## Технологии

### Backend
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5 + Mockito

### Frontend
- React 18
- TypeScript
- Recharts (графики)
- Framer Motion (анимации)
- React Router

### DevOps
- Docker & Docker Compose
- Nginx (для фронтенда)
- Health Checks

## Разработка

### Требования
- Docker & Docker Compose
- Java 17+ (для локальной разработки)
- Node.js 18+ (для локальной разработки)

### Запуск в режиме разработки

Backend:
```bash
mvn spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
npm start
```

### Тестирование

Запуск тестов:
```bash
mvn test
```

## База данных

PostgreSQL с автоматической миграцией схемы.

**Подключение:**
- Host: localhost:5432
- Database: newsdb
- User: newsuser
- Password: newspass

## Мониторинг

- Health checks для всех сервисов
- Actuator endpoints для мониторинга backend
- Автоматический restart контейнеров

### Запуск с разными профилями

**Development (default):**
```bash
mvn spring-boot:run
```

**Production:**
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

**Tests:**
```bash
mvn test
```

**Docker:**
```bash
docker-compose up -d
```

### Безопасность

- Секретные ключи GigaChat не должны храниться в коде
- Используйте переменные окружения для production
- application-prod.properties содержит примеры ключей (замените на реальные)
