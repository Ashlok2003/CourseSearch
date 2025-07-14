APP_NAME=coursesearch
DOCKER_COMPOSE=docker compose
DOCKER_IMAGE=$(APP_NAME):latest

.PHONY: help
help:
	@echo ""
	@echo "Usage:"
	@echo "  make build         - Build Spring Boot jar locally"
	@echo "  make run           - Run Spring Boot app locally (needs ES running)"
	@echo "  make docker-build  - Build Docker image (multi-stage)"
	@echo "  make up            - Start all services with Docker Compose"
	@echo "  make down          - Stop all services"
	@echo "  make logs          - Show app & ES logs"
	@echo "  make clean         - Remove Docker image & containers"
	@echo ""

build:
	./mvnw clean package -DskipTests

run:
	java -jar target/*.jar

docker-build:
	docker build -t $(DOCKER_IMAGE) .

up:
	$(DOCKER_COMPOSE) up --build -d

down:
	$(DOCKER_COMPOSE) down

logs:
	$(DOCKER_COMPOSE) logs -f

clean:
	$(DOCKER_COMPOSE) down -v --rmi local
	docker rmi -f $(DOCKER_IMAGE) || true
