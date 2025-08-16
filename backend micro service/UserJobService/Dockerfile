# Simple Dockerfile for UserJob Service
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/UserJobService-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]