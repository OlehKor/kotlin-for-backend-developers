# Dog Breed Service

This service provides information about various dog breeds using the Dog CEO API. It's built with Spring WebFlux, Kotlin Coroutines, and R2DBC.

## Features

- Retrieve all dog breeds
- Get all sub-breeds
- Get breeds without sub-breeds
- Get sub-breeds for a specific breed
- Get and cache breed images
- Swagger UI documentation
- Prometheus metrics support
- Caching with Caffeine
- Global exception handling

## Prerequisites

- JDK 21
- Maven 3.6+

## Running the Service

1. Clone the repository
2. Navigate to the project directory
3. Run the service:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Documentation

Once the service is running, you can access:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI documentation: http://localhost:8080/api-docs

## Endpoints

- `GET /api/breeds` - Get all breeds
- `GET /api/breeds/sub-breeds` - Get all sub-breeds
- `GET /api/breeds/no-sub-breeds` - Get breeds without sub-breeds
- `GET /api/breeds/{breed}/sub-breeds` - Get sub-breeds for a specific breed
- `GET /api/breeds/{breed}/image` - Get image for a specific breed

## Monitoring

The service exposes the following actuator endpoints:
- Health check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## Testing

To run the tests:
```bash
./mvnw test
```

## Implementation Details

- Uses Kotlin Coroutines for asynchronous programming
- Spring WebFlux with WebClient for reactive HTTP calls
- R2DBC for reactive database access
- Caffeine for caching
- MockK for unit testing
- JUnit 5 for testing
- Global exception handling for consistent error responses
- Swagger documentation with OpenAPI 3.0



