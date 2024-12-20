# Use an OpenJDK 17 base image
FROM openjdk:17-jdk-slim

LABEL authors="prasaddasari"

# Set the working directory inside the container
WORKDIR /app

# Accept the build argument for the version
ARG APP_VERSION

# Set the version for the application (default to v1)
ENV APP_VERSION=${APP_VERSION}

# Copy the JAR file built by Gradle into the container
COPY build/libs/hello-world-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
