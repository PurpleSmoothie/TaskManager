# Этап сборки
FROM maven:3.9.6-eclipse-temurin AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл зависимостей и загружаем их отдельно для кеша
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем остальной код проекта
COPY . .

# Сборка проекта + прогон всех тестов
RUN mvn clean verify

# Этап запуска
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Копируем готовый JAR из builder-слоя
COPY --from=builder /app/target/task-manager-*.jar app.jar

# Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]