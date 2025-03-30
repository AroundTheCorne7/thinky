# Use a lightweight JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR into the container
COPY build/libs/thinky.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
