kill-ports: ## Kill ports
	kill-port 8877
	kill-port 8878
	@echo "Killed ports"

docker-up: ## Docker up
	docker-compose up -d
	@echo "Docker up"