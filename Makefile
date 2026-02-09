
up: down
	@docker compose up -d keycloak
	@docker compose up terraform --build

down:
	@docker compose down
	@docker image ls | grep church-crm | awk '{print $$3}' | xargs docker rmi -f || true

test: docker-up
	mvn clean compile verify