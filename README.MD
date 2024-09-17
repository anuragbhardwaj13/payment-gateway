# Payment Gateway

This project is a payment gateway application built with Spring Boot and PostgreSQL.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Docker and Docker Compose

## Getting Started

1. Clone the repository:

   ```
   git clone https://github.com/anuragbhardwaj13/payment-gateway.git
   cd payment-gateway
   ```

2. Create a `docker-compose.yml` file in the project root directory and copy the following content:

   ```yaml
   version: '3.8'

   services:
     app:
       build: .
       ports:
         - "8080:8080"
         - "5005:5005"  # For remote debugging
       volumes:
         - ./src:/app/src  # Map the source code to the container
         - ./target:/app/target  # Map the target directory to the container
       depends_on:
         - db
       environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/paymentgateway_db
         SPRING_DATASOURCE_USERNAME: your_username
         SPRING_DATASOURCE_PASSWORD: your_password
         SPRING_JPA_HIBERNATE_DDL_AUTO: update
         SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT: org.hibernate.dialect.PostgreSQLDialect
         SPRING_DEVTOOLS_REMOTE_SECRET: your_secret
         SPRING_SECURITY_KEY: your_security_key

     db:
       image: postgres:16
       restart: always
       ports:
         - "5432:5432"
       volumes:
         - ./postgres-data:/var/lib/postgresql/data
       environment:
         - POSTGRES_PASSWORD=your_password
         - POSTGRES_USER=your_username
         - POSTGRES_DB=paymentgateway_db

     adminer:
       image: adminer
       restart: always
       ports:
         - "8081:8080"

   volumes:
     postgres-data:
   ```

3. Build the project:

   ```
   ./mvnw clean package -DskipTests
   ```

4. Start the application using Docker Compose:

   ```
   docker-compose down
   docker-compose up --build
   ```

   This will start the payment gateway application, PostgreSQL database, and Adminer (a web-based database management tool).

5. Access the application:

    - Payment Gateway: http://localhost:8080
    - Adminer (Database GUI): http://localhost:8081

## Configuration

The application configuration can be modified in the `docker-compose.yml` file. Update the following environment variables with your desired values:

- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_DEVTOOLS_REMOTE_SECRET`: Secret for remote debugging
- `SPRING_SECURITY_KEY`: Security key for the application


## Database Management

Adminer, a web-based database management tool, is included in the Docker Compose setup. You can access it at http://localhost:8081 to manage the PostgreSQL database.

## Troubleshooting

If you encounter any issues, try the following:

- Ensure that the required ports (8080, 5005, 5432, 8081) are not being used by other applications.
- Verify that Docker and Docker Compose are installed correctly and have the necessary permissions.
- Check the logs of the Docker containers for any error messages.

## License

This project is licensed under the [MIT License](LICENSE).