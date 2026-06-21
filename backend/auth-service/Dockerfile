# ==========================================
# Stage 1: Build the Executable Fat JAR
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# 1. Copy the parent POM
COPY backend/pom.xml ./pom.xml

# 2. Copy the specific sub-module directory contents
COPY backend/auth-service/ ./auth-service/

# 3. Compile the package
WORKDIR /build/auth-service
RUN mvn clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=3

# 4. Standardize the output fat JAR name
RUN rm -f target/original-*.jar && mv target/*.jar target/app.jar

# ==========================================
# Stage 2: Clean, Lightweight Production Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application

COPY --from=builder /build/auth-service/target/app.jar ./app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
