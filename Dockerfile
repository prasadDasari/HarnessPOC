# Use an OpenJDK 17 base image
FROM openjdk:17-jdk-slim

LABEL authors="prasaddasari"

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built by Gradle into the container
COPY build/libs/hello-world-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
