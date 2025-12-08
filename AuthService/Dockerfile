FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY /target/AuthService-0.0.1-SNAPSHOT.jar /app/AuthService-0.0.1-SNAPSHOT.jar

EXPOSE 9091

ENTRYPOINT ["java","-jar","AuthService-0.0.1-SNAPSHOT.jar"]