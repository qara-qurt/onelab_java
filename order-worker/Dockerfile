FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

WORKDIR /app/order-worker
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/order-worker/target/*.jar order-worker.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "order-worker.jar"]
