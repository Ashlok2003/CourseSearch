# Course Search Application with Elasticsearch

## Overview
This Spring Boot application provides a search interface for educational courses with filtering, sorting, and pagination capabilities. It uses Elasticsearch as the search engine and includes optional autocomplete and fuzzy search features.

## Prerequisites
- Docker (for running Elasticsearch)
- Java 17+
- Maven

## Setup Instructions

### 1. Start Elasticsearch
```bash
docker compose up -d
```

Verify Elasticsearch is running:
```bash
curl http://localhost:9200
```

### 2. Build and Run the Application
```bash
mvn clean package
java -jar target/*.jar
```

Alternatively, use the Makefile commands:
```bash
make build
make run
```

Or run everything with Docker:
```bash
make up
```

## API Documentation
The application provides Swagger UI for interactive API testing:
- Access at: http://localhost:8080/swagger-ui

## Sample Requests

### Basic Search
```bash
curl "http://localhost:8080/api/v1/search?q=algebra&category=Math&minAge=10&maxAge=15&page=0&size=5"
```

### Search with Price Range
```bash
curl "http://localhost:8080/api/v1/search?minPrice=50&maxPrice=100&sort=priceAsc"
```

### Upcoming Courses
```bash
curl "http://localhost:8080/api/v1/search?startDate=2025-07-10"
```

### Autocomplete Suggestions
```bash
curl "http://localhost:8080/api/v1/search/suggest?q=rob"
```

### Fuzzy Search
```bash
curl "http://localhost:8080/api/v1/search?q=dinors"
```

## Data Initialization
The application automatically loads sample data from `src/main/resources/sample-courses.json` on startup.

To verify data is loaded:
```bash
curl "http://localhost:8080/api/v1/search?size=1"
```

## Project Structure
- `src/main/java/com/example/coursesearch/`
  - `config/` - Elasticsearch & OpenAPI configuration
  - `controller/` - REST endpoints
  - `model/` - Elasticsearch entity mappings
  - `dto/` - Data transfer objects
  - `repository/` - Elasticsearch repositories
  - `exceptions/` - Application Global & Custom error handling
  - `service/` - Business logic
- `src/main/resources/` - Configuration files and sample data
- `Dockerfile` - Application Dockerfile
- `Makefile` -  For automating common application tasks
- `docker-compose.yml` - Elasticsearch container definition

## Cleanup
To stop all services and clean up:
```bash
make down
make clean
```

## Bonus Features
- Autocomplete suggestions for course titles
- Fuzzy matching for search queries
- Swagger UI documentation
- Dockerized deployment option
