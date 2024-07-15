# Use a specific version of Maven and OpenJDK for the build stage
FROM maven:3.8.5-openjdk-17 AS build
# Copy the entire project into the Docker image
COPY . .
# Run the Maven command to clean and package the application, skipping tests
RUN mvn clean package -DskipTests

# Use a slim version of OpenJDK for the final stage
FROM openjdk:17.0.1-jdk-slim
# Copy the packaged application from the build stage
COPY --from=build /target/springboot-bidding-app-api-0.0.1-SNAPSHOT.jar springboot-bidding-app-api.jar
# Expose port 8080 for the application
EXPOSE 8080
# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "springboot-bidding-app-api.jar"]
