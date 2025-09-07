# Use OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /src

# Copy built jar into the container
COPY target/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
