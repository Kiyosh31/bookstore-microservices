# Overview
This is my learning in creating microservices in spring boot with several technologies as `kafka`, `Eureka`, `Config Server` etc...

# Pre-requisites
1. Java 17
2. Intellij idea
3. docker desktop

# Usage
Run the services in order
1. discovery-server
2. config-server
3. docker-compose
4. all services
5. api-gateway

# Run
In root folder there is a `.run` folder you can import in intellij idea to run all microservices at once.

Otherwise, run services as described in usage section

# Info
> Note: once running the project (localhost)

- [Eureka available here](http://localhost:8761/)

## Swagger

- [user-service](http://localhost:8081/swagger-ui/index.html)
- [book-service](http://localhost:8082/swagger-ui/index.html)
- [order-service](http://localhost:8084/swagger-ui/index.html)
- [stock-service](http://localhost:8083/swagger-ui/index.html)
- [notification-service](http://localhost:8085/swagger-ui/index.html)

# Note
if for somehow the kafka communication starts to fail, go to event class and re-write `@AllArgsConstructor` annotation that will fix everything