#!/bin/bash
set -e

echo "========================================="
echo "🔐 Building Custom Keycloak Image"
echo "========================================="

ROOT_DIR="${PROJECT_ROOT:-/project}"
KEYCLOAK_DIR="$ROOT_DIR/config/keycloak"

if [ ! -d "$KEYCLOAK_DIR" ]; then
    echo "⚠️  Keycloak directory not found: $KEYCLOAK_DIR"
    echo "Skipping Keycloak build"
    exit 0
fi

cd "$KEYCLOAK_DIR"

# Проверяем наличие Dockerfile
if [ ! -f "Dockerfile" ]; then
    echo "❌ Dockerfile not found in $KEYCLOAK_DIR"
    exit 1
fi

# Проверяем наличие SPI провайдера
if [ ! -d "spi-providers" ] || [ -z "$(ls -A spi-providers/*.jar 2>/dev/null)" ]; then
    echo "⚠️  No SPI providers found in spi-providers/"
    echo "Make sure build-spi.sh ran successfully"
fi

# Сборка образа
IMAGE_NAME="${DOCKER_REGISTRY:-docker.io}/${DOCKER_USERNAME}/keycloak-custom:${VERSION:-latest}"
echo "🐳 Building Keycloak image: $IMAGE_NAME"

docker build -t $IMAGE_NAME .

# Пуш образа
if [ -n "$DOCKER_USERNAME" ]; then
    echo "📤 Pushing Keycloak image: $IMAGE_NAME"
    docker push $IMAGE_NAME
fi

echo "✅ Keycloak image built and pushed successfully"
echo ""