# ---- STEP 1: Build the app ----
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy source code
COPY . .

# Build the Spring Boot app (creates target/*.jar)
RUN ./mvnw clean package -DskipTests

# ---- STEP 2: Run the app ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional)
EXPOSE 8080

# Start app
ENTRYPOINT ["java", "-jar", "app.jar"]
