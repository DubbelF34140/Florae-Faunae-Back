# Étape 1: Build avec Gradle
FROM gradle:7.6.2-jdk24 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Étape 2: Run Spring Boot + SSL
FROM openjdk:24-jdk-slim
WORKDIR /app

# Copier le JAR
COPY --from=build /app/build/libs/*.jar app.jar

# Exposer le port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
