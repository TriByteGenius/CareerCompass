# Simple Dockerfile for Job Service
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/JobService-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]