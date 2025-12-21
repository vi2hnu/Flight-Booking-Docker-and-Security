FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/FlightService-0.0.1-SNAPSHOT.jar /app/FlightService-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "FlightService-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
