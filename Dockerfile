
FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests


FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/springboot-bidding-app-api-0.0.1-SNAPSHOT.jar springboot-bidding-app-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "springboot-bidding-app-api.jar"]
