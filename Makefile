# -----------
# Run
# -----------
run-local:
	mvn clean package -Dmaven.test.skip=true; \
    docker-compose rm -s --force; \
	docker-compose up -d; \
	mvn spring-boot:run


# -----------
# Build
# -----------
build:
	docker-compose up -d; \
	mvn clean package; \
	docker-compose rm -s --force


# -----------
# Tests
# -----------
test:
	docker-compose up -d; \
	mvn clean test; \
	docker-compose rm -s --force


# -----------
# Test DB
# -----------
start-testdb:
	docker-compose up -d sales-db

stop-testdb:
	docker-compose rm -s --force sales-db


# -----------
# CircleCI
# -----------
ci-validate:
	circleci config validate

.PHONY: run-local build test start-testdb stop-testdb ci-validate
