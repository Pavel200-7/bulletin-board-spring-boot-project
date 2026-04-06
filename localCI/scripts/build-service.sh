#!/bin/bash
set -e

SERVICE_NAME=$1

if [ -z "$SERVICE_NAME" ]; then
    echo "❌ Usage: build-service.sh <service-name>"
    exit 1
fi

echo "========================================="
echo "🐳 Building Service: $SERVICE_NAME"
echo "========================================="

ROOT_DIR="${PROJECT_ROOT:-/project}"

# Пути к сервисам
SERVICE_PATHS=(
    "$ROOT_DIR/service/$SERVICE_NAME"
    "$ROOT_DIR/$SERVICE_NAME"
    "service/$SERVICE_NAME"
    "../service/$SERVICE_NAME"
)

SERVICE_PATH=""
for path in "${SERVICE_PATHS[@]}"; do
    if [ -d "$path" ]; then
        SERVICE_PATH="$path"
        echo "✅ Found service at: $SERVICE_PATH"
        break
    fi
done

if [ -z "$SERVICE_PATH" ]; then
    echo "❌ Service '$SERVICE_NAME' not found!"
    exit 1
fi

cd "$SERVICE_PATH"

# Сборка jar
if [ -f "gradlew" ]; then
    ./gradlew clean bootJar -x test
elif [ -f "build.gradle" ]; then
    gradle clean bootJar -x test
elif [ -f "mvnw" ]; then
    ./mvnw clean package -DskipTests
elif [ -f "pom.xml" ]; then
    mvn clean package -DskipTests
fi

# Сборка Docker образа
IMAGE_NAME="${DOCKER_REGISTRY:-docker.io}/${DOCKER_USERNAME}/$SERVICE_NAME:${VERSION:-latest}"
echo "Building image: $IMAGE_NAME"
docker build -t $IMAGE_NAME .

# Пуш
if [ -n "$DOCKER_USERNAME" ]; then
    echo "Pushing image: $IMAGE_NAME"
    docker push $IMAGE_NAME
fi

echo ""