FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/springboot-bidding-app-api-*-SNAPSHOT.jar bidding-api.jar
ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8081
ENTRYPOINT ["java","-jar","bidding-api.jar"]
