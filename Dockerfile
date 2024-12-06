# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app

# Copy the project to the image
COPY . /app

# Run Maven to package the project and generate the .jar
RUN mvn clean install -DskipTests

# Use a lighter image for the final container
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the build container
COPY --from=build /app/target/Backend_Wakanda_Salud-0.0.1-SNAPSHOT.jar /app/backend_wakanda_salud.jar

# Download the wait-for-it.sh script
RUN apt-get update && apt-get install -y curl && \
    curl -o /app/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /app/wait-for-it.sh

# Expose the port the server will listen on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["./wait-for-it.sh", "mysql-wakanda:3306", "--", "java", "-jar", "/app/backend_wakanda_salud.jar"]