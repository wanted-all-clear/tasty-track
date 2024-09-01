# Use OpenJDK 17 slim image as base for the build stage
FROM openjdk:17-jdk-slim AS build

# Set the working directory in the container
WORKDIR /app

# Define an argument for the JAR file location
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR file from the build context to the container
COPY ${JAR_FILE} app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Copy environment variables file to the container
COPY .env .env

# Define the entrypoint for the container
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dotel.resource.attributes=service.name=auth-server", "-jar", "app.jar"]
