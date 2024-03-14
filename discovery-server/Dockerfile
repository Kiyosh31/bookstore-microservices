# Create build
FROM maven:3.8.3-jdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn package -DskipTests

# Create app
FROM openjdk:17

WORKDIR /app
COPY target/discovery-server-0.0.1.jar /app/discovery-server.jar

ENTRYPOINT ["java", "-jar", "discovery-server.jar"]