# Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Gradle wrapper and config
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy source code
COPY src src

# Build the Spring Boot JAR
RUN chmod +x gradlew && ./gradlew bootJar

# Render uses port 8080
EXPOSE 8080

# Run the app (shell form allows wildcard expansion)
CMD java -jar build/libs/*.jar
