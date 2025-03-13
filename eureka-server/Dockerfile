# Этап сборки
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["sh", "-c", "java -jar /app/eureka-server.jar && sh"]
