# Container za izgradnju (build) aplikacije
FROM amazoncorretto:17-alpine-jdk


ENV DB_URL=jdbc:postgresql://dpg-cl4krus72pts739i2rvg-a:5432/prijava_ostecenja_db
ENV DB_USERNAME=prijava_ostecenja_db_user
ENV DB_PASS=KT9yduovllMpa8qQsZH3PKTn2AAM3t93
ENV DB_DRIVER=org.postgresql.Driver
# Set the working directory in the container
WORKDIR /app

# Add the application JAR file to the container
COPY target/prijave.jar /app/prijave.jar

# Expose the port that your application runs on
EXPOSE 8080

# Specify the command to run the service
CMD ["java", "-jar", "/app/prijave.jar"]