FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

WORKDIR /app/user_service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user_service/target/*.jar user_service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user_service.jar"]
