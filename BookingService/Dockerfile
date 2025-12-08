FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/BookingService-0.0.1-SNAPSHOT.jar /app/BookingService-0.0.1-SNAPSHOT.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "BookingService-0.0.1-SNAPSHOT.jar"]