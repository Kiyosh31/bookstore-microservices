FROM openjdk:17

WORKDIR /app
COPY target/api-gateway-0.0.1.jar /app/api-gateway.jar

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]

