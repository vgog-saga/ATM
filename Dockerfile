# Use a base image with a JDK
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app/

# Expose the port your app runs on
EXPOSE 8080

# Specify the command to run on container startup
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
