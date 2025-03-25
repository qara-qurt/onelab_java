FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

# Собираем сам сервис
WORKDIR /app/gateway_cli_service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/gateway_cli_service/target/*.jar gateway_cli_service.jar
ENTRYPOINT ["sh", "-c", "java -jar gateway_cli_service.jar && sh"]