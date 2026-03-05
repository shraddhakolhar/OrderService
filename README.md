Order Service

The Order Service is responsible for handling order creation and order management in the e-commerce system. It processes checkout requests, creates orders for authenticated users, and updates order status after payment confirmation.


The service uses Spring Security with JWT authentication, Spring Data JPA with MySQL for persistence, Flyway for database migrations, and Kafka for publishing order-related events. Dependencies and build configuration are managed using Maven. 

Configuration values such as database credentials, Kafka servers, service ports, and JWT secrets are externalized using environment variables, making the service easier to deploy in different environments.

To run the service, ensure that Eureka Server, MySQL, and Kafka are running. Then build and start the application using Maven:

mvn clean install
mvn spring-boot:run

By default, the service runs on port 8483.

The Order Service exposes REST endpoints for order operations. The controller responsible for these endpoints is implemented in the service layer of the application. 

OrderController

Main endpoints include:

POST /orders – Create a new order (checkout)

POST /orders/{orderId}/paid – Mark order as paid (payment webhook)

GET /orders/{orderId} – Retrieve order details

Flyway is used for database schema migrations, and migration scripts should be placed inside the src/main/resources/db/migration directory.

The Order Service coordinates with other microservices such as Cart Service, Product Service, and Payment systems to complete the order workflow.
