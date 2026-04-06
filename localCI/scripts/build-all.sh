#!/bin/bash
set -e

echo "========================================="
echo "🚀 CI/CD Pipeline Started"
echo "========================================="
echo "Version: ${VERSION:-latest}"
echo "Registry: ${DOCKER_REGISTRY:-docker.io}"
echo "Username: ${DOCKER_USERNAME:-not set}"
echo "Project root: ${PROJECT_ROOT:-/project}"
echo ""

# Устанавливаем корень проекта
export PROJECT_ROOT="${PROJECT_ROOT:-/project}"

# Шаг 1: Авторизация
/usr/local/bin/docker-login.sh

# Шаг 2: Сборка общей библиотеки
/usr/local/bin/build-shared-lib.sh

# Шаг 3: Сборка SPI
if [ -f "/usr/local/bin/build-spi.sh" ]; then
    /usr/local/bin/build-spi.sh
fi

# Шаг 4: Сборка фронтенда
if [ -f "/usr/local/bin/build-client.sh" ]; then
    /usr/local/bin/build-client.sh
fi

# Шаг 5: Сборка всех микросервисов
SERVICES=("eureka" "apigateway" "auth" "bulletin" "chat" "notification")

for service in "${SERVICES[@]}"; do
    if [ -d "$PROJECT_ROOT/service/$service" ]; then
        /usr/local/bin/build-service.sh $service
    fi
done

# Шаг 6: Сборка кастомного Keycloak
if [ -f "/usr/local/bin/build-keycloak.sh" ]; then
    /usr/local/bin/build-keycloak.sh
fi

echo "========================================="
echo "✅ CI/CD Pipeline Completed Successfully"
echo "========================================="