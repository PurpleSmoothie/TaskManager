# Task Manager API

REST API-сервис для управления задачами, разработанный на Java и Spring Boot. Поддерживает авторизацию через JWT, разграничение прав по ролям, фильтрацию задач и комментарии.

## 📌 Описание

Система управления задачами, реализующая:

- создание, обновление, удаление и просмотр задач;
- установку статуса (в ожидании, в процессе, завершено) и приоритета (высокий, средний, низкий);
- прикрепление комментариев;
- разграничение доступа по ролям (администратор и пользователь);
- поиск задач по автору или исполнителю;
- фильтрацию и пагинацию списка задач.

## 🚀 Технологии

- Java 17+
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Maven
- Docker + Docker Compose
- Swagger / OpenAPI
- JUnit + MockMvc

## ⚙️ Запуск проекта

### Локальный запуск

1. Установите зависимости:
   ```bash
   ./mvnw clean install
   ```

2. Настройте базу данных:

Создайте базу данных PostgreSQL или MySQL.

Обновите настройки в `application.properties` или `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/task_db
    username: your_username
    password: your_password
```

3. Запустите приложение:

```bash
./mvnw spring-boot:run
```

### Запуск через Docker

1. Соберите Docker-образ:

```bash
docker build -t task-manager .
```

2. Запустите контейнер:

```bash
docker-compose up --build
```

3. Swagger UI будет доступен по адресу:

```
http://localhost:8080/swagger-ui/index.html
```
## 🧑‍💻 Демо-пользователи для тестирования
Для удобства тестирования в базу данных автоматически добавляются 3 пользователя с разными ролями:

Email	              Роль	       Логин	        Пароль
admin@mail.com	      ADMIN	       test-admin	    test-admin
user@mail.com	      USER	       test-user	    test-user
moderator@mail.com	  MODERATOR	   test-moderator	test-moderator

## 🔐 Аутентификация и роли

Аутентификация осуществляется по `email` и `password`. После логина выдается JWT, который передаётся в заголовке `Authorization: Bearer <token>`.

### Поддерживаемые роли:

- `ROLE_ADMIN` — полный доступ ко всем функциям.
- `ROLE_MODERATOR` — управление задачами и комментариями.
- `ROLE_USER` — доступ только к своим задачам и комментариям.

## 🧩 Возможности API

- Регистрация и вход
- CRUD-операции над задачами
- Назначение исполнителя
- Комментирование задач
- Фильтрация задач по статусу, приоритету, автору, исполнителю
- Пагинация
- Проверка прав доступа
- Валидация всех входящих данных
- Унифицированная обработка ошибок
- Swagger-документация

## 🧪 Тестирование

Для запуска тестов используйте:

```bash
./mvnw test
```

Покрытие тестами включает:

- Аутентификацию и авторизацию
- Управление задачами и комментариями
- Обработку ошибок и валидацию

## 🐳 Docker Compose

Пример `docker-compose.yml`:

```yaml
version: '3'
services:
  db:
    image: postgres:latest
    environment:
      POSTGRES_DB: task_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/task_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    ports:
      - "8080:8080"
    depends_on:
      - db
```

## 🗂 Структура проекта

```
task-manager/
├── Dockerfile
├── docker-compose.yml
├── init.sql
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/com/tema_kuznetsov/task_manager/
│   └── test/
│       └── java/com/tema_kuznetsov/task_manager/
```

## ⚙️ Переменные окружения

```properties
spring.datasource.url=jdbc:postgresql://db:5432/task_db
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=your_jwt_secret
```

## ⚠️ Обработка ошибок

Система возвращает структурированные и понятные ошибки, включая:

- Ошибки валидации (400 Bad Request)
- Ошибки доступа (403 Forbidden)
- Ошибки "не найдено" (404 Not Found)
- Ошибки аутентификации (401 Unauthorized)
