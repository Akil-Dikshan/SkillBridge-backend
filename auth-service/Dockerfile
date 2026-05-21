
# Stage 1: Build

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first — Docker caches this layer
# So if only source code changes, Maven doesn't re-download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the jar
COPY src ./src
RUN mvn package -DskipTests -B


# Stage 2: Runtime

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port auth-service runs on
EXPOSE 8081

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]