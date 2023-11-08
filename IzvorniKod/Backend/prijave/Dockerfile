# Container za izgradnju (build) aplikacije
FROM amazoncorretto:17-alpine-jdk


ENV DB_URL=jdbc:postgresql://dpg-cl5tb756fh7c73evcb10-a:5432/prijava_ostecenja_db_413x
ENV DB_USERNAME=prijava_ostecenja_db_user
ENV DB_PASS=9OponSzIox4w8bqGRni1aoEgX77LRBY5
ENV DB_DRIVER=org.postgresql.Driver
# Set the working directory in the container
WORKDIR /app

# Add the application JAR file to the container
COPY target/prijave.jar /app/prijave.jar

# Expose the port that your application runs on
EXPOSE 8080

# Specify the command to run the service
CMD ["java", "-jar", "/app/prijave.jar"]