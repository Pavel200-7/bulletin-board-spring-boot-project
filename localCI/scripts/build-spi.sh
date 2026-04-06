#!/bin/bash
set -e

echo "========================================="
echo "🔌 Building Keycloak SPI"
echo "========================================="

ROOT_DIR="${PROJECT_ROOT:-/project}"

# Путь к SPI
SPI_PATH="$ROOT_DIR/service/lib/keycloak-registration-listener-spi"

if [ ! -d "$SPI_PATH" ]; then
    echo "⚠️  SPI directory not found: $SPI_PATH"
    echo "Skipping SPI build"
    exit 0
fi

cd "$SPI_PATH"

# Сборка SPI
echo "📦 Building SPI JAR"
if [ -f "gradlew" ]; then
    ./gradlew clean build -x test
elif [ -f "build.gradle" ]; then
    gradle clean build -x test
else
    echo "❌ No build file found"
    exit 1
fi

# Копируем собранный JAR в директорию keycloak
KEYCLOAK_SPI_DIR="$ROOT_DIR/config/keycloak/spi-providers"
mkdir -p "$KEYCLOAK_SPI_DIR"

# Копируем JAR (не sources и не javadoc)
find build/libs -name "*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" -exec cp {} "$KEYCLOAK_SPI_DIR/" \;

echo "✅ SPI built and copied to $KEYCLOAK_SPI_DIR"
echo ""