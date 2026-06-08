FROM eclipse-temurin:21-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file from your target folder into the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot application listens on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]