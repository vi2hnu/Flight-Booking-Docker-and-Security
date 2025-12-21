#!/bin/bash
set -e

java -jar ./ServiceRegistry/target/ServiceRegistry-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
sleep 10

java -jar ./ConfigServer/target/ConfigServer-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
sleep 10

java -jar ./AuthService/target/AuthService-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
java -jar ./FlightService/target/FlightService-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
java -jar ./BookingService/target/BookingService-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
java -jar ./EmailService/target/EmailService-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &
sleep 5

java -jar ./ApiGateway/target/ApiGateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &

wait
