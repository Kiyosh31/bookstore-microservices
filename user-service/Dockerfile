FROM openjdk:17

RUN mvn clean package
RUN mvn package

WORKDIR /app
COPY target/user /app/user-service.jar

ENTRYPOINT ["java", "-jar", "user-service.jar"]