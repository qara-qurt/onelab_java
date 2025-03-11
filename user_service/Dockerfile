FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar user_service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user_service.jar"]
