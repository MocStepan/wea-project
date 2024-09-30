# Use an official Gradle image to build the backend
FROM gradle:jdk20 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the backend folder only (since gradlew is there)
COPY . /app/

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Run the Gradle build to create the application JAR
RUN ./gradlew build

# Create a new stage to run the application
FROM openjdk:20-jdk-slim
ENV SPRING_PROFILES_ACTIVE=localDev
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/wea_project
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
# Set the working directory in the new stage
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the port the backend runs on (e.g., 8080)
EXPOSE 8080

# Run the application JAR
CMD ["java", "-jar", "/app/app.jar"]
