FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/springboot-bidding-app-api-*.jar bidding-api.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","bidding-api.jar"]
