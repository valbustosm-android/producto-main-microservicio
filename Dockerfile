# ETAPA 1: Compilar el proyecto con Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Imagen liviana para ejecutar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/producto-main-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
USER nobody
ENTRYPOINT ["java", "-jar", "app.jar"]
