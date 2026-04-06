#!/bin/bash
set -e

echo "========================================="
echo "🎨 Building Vue.js Frontend"
echo "========================================="

ROOT_DIR="${PROJECT_ROOT:-/project}"
CLIENT_DIR="$ROOT_DIR/client"

if [ ! -d "$CLIENT_DIR" ]; then
    echo "⚠️  Client directory not found: $CLIENT_DIR"
    echo "Skipping frontend build"
    exit 0
fi

cd "$CLIENT_DIR"

# Проверяем package.json
if [ ! -f "package.json" ]; then
    echo "❌ package.json not found"
    exit 1
fi

# Сборка образа
IMAGE_NAME="${DOCKER_REGISTRY:-docker.io}/${DOCKER_USERNAME}/frontend:${VERSION:-latest}"
echo "🐳 Building frontend image: $IMAGE_NAME"

# Используем BuildKit для кеширования
DOCKER_BUILDKIT=0 docker build \
    --cache-from $IMAGE_NAME \
    -t $IMAGE_NAME \
    -t ${DOCKER_REGISTRY:-docker.io}/${DOCKER_USERNAME}/frontend:latest \
    .

# Пуш
if [ -n "$DOCKER_USERNAME" ]; then
    echo "📤 Pushing frontend image: $IMAGE_NAME"
    docker push $IMAGE_NAME
    docker push ${DOCKER_REGISTRY:-docker.io}/${DOCKER_USERNAME}/frontend:latest
fi

echo "✅ Frontend image built and pushed successfully"
echo ""