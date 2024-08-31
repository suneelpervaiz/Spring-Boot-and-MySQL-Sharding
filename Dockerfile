# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the host to the container
COPY target/shard-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
