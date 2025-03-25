FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

WORKDIR /app/restaurant_service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/restaurant_service/target/*.jar restaurant_service.jar
ENTRYPOINT ["java", "-jar", "restaurant_service.jar"]
