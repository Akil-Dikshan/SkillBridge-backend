FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first so dependency download is cached separately from source changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Must match server.port in application.yaml
EXPOSE 8084

# Exec form makes Java PID 1 — receives OS signals for graceful shutdown
ENTRYPOINT ["java", "-jar", "app.jar"]
