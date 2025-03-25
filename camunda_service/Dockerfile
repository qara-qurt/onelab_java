FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

WORKDIR /app/camunda_service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/camunda_service/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
