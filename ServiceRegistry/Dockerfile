FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/ServiceRegistry-0.0.1-SNAPSHOT.jar /app/ServiceRegistry-0.0.1-SNAPSHOT.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "ServiceRegistry-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
