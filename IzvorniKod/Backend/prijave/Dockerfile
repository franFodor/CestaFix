# Container za izgradnju (build) aplikacije
FROM amazoncorretto:17-alpine-jdk

# Set the working directory in the container
WORKDIR /app

# Add the application JAR file to the container
COPY target/prijave.jar /app/prijave.jar

# Expose the port that your application runs on
EXPOSE 8080

# Specify the command to run the service
CMD ["java", "-jar", "/app/prijave.jar"]