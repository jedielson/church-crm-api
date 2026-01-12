
docker-up: docker-down
	@docker compose up -d keycloak
	@docker compose up terraform --build

docker-down:
	@docker compose down
	@docker image ls | grep church-cms-terraform | awk '{print $$3}' | xargs docker rmi -f || true
