FROM openjdk:17-jdk-slim AS build
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
COPY .env .env
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dotel.resource.attributes=service.name=auth-server","-jar","app.jar"]

