# Order Service

## Project Overview
Order Service is a backend service designed to manage customer orders within a purchase process. It is implemented using **Spring Boot**, **Spring Data JPA**, and **MySQL** as the database. Asynchronous communication with a **Notification Service** is handled through **RabbitMQ**. The service manages two core entities: **Order** and **OrderItems**, providing complete CRUD functionalities.

## Key Features
- **Order and OrderItems Management**: Includes CRUD endpoints for both `Order` and `OrderItems` entities.
- **Asynchronous Communication**: Sends data to a Notification Service via RabbitMQ for decoupled and asynchronous message handling.
- **SAGA Pattern for Transactional Consistency**: Uses a SAGA pattern to coordinate transactions across multiple services, ensuring rollback on failure in any step of the ordering process.
- **Synchronous Inter-Service Communication**: Interacts with other services (Store, Product, and Bank) using synchronous REST calls to:
  - **Store Service**: Consumes stock for ordered products.
  - **Product Service**: Fetches product details along with pricing.
  - **Bank Service**: Manages payment transactions between customers and merchants.

## Tech Stack
- **Spring Boot**: For building the service layer and RESTful API.
- **Spring Data JPA**: For database interactions with MySQL.
- **RabbitMQ**: To handle asynchronous notifications.
- **Docker**: Containerized the application, available on DockerHub.
- **MySQL**: Database to store orders and related data.

## Architecture and Workflow
1. **Order Creation Process**: When an order is placed, the service coordinates with:
   - **Store Service** to reserve the required stock.
   - **Product Service** to fetch product details and prices.
   - **Bank Service** to handle payments.
   
   These communications are performed using **RestTemplate** for synchronous calls.
   
2. **Transactional Consistency with SAGA**: If any step fails during the order creation process, the SAGA pattern initiates a rollback, reversing the steps successfully completed until the failure point.

3. **Asynchronous Notifications**: Once an order is confirmed, an event is sent to the Notification Service through RabbitMQ, ensuring non-blocking communication for better performance.

## Deployment
The application has been dockerized, and the Docker image is available on DockerHub for easy deployment.

## Run Order service as a docker Container
   - First pull image (optional):
     ```bash
       docker image pull amirelkased/order_service:v1.5
     ```
   - create a container from this image:
     ```bash
        docker container run -d --rm --name order-service -p 8080:8080 amirelkased/order_service:v1.5
     ```

## Usage
1. **Starting the Application**: Use Docker to run the service, ensuring RabbitMQ, MySQL, and other required services are up and running.
2. **Endpoints**: The service exposes CRUD endpoints for managing `Order` and `OrderItems` entities.
