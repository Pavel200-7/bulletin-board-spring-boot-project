#!/bin/bash
set -e

echo "========================================="
echo "📚 Building Shared Library (Contracts)"
echo "========================================="

# Показываем текущую директорию для отладки
echo "Current directory: $(pwd)"
echo "Project root: ${PROJECT_ROOT:-/project}"

# Используем PROJECT_ROOT если задан, иначе /project, иначе текущая директория
ROOT_DIR="${PROJECT_ROOT:-/project}"

# Проверяем разные возможные пути к библиотеке
LIB_PATHS=(
    "$ROOT_DIR/service/lib/rabbitMQ-events-contracts"
    "$ROOT_DIR/service/lib/rabbitmq-events-contracts"
    "service/lib/rabbitMQ-events-contracts"
    "../service/lib/rabbitMQ-events-contracts"
)

LIB_PATH=""
for path in "${LIB_PATHS[@]}"; do
    if [ -d "$path" ]; then
        LIB_PATH="$path"
        echo "✅ Found shared library at: $LIB_PATH"
        break
    fi
done

if [ -z "$LIB_PATH" ]; then
    echo "❌ Shared library directory not found!"
    echo "Searched in:"
    for path in "${LIB_PATHS[@]}"; do
        echo "  - $path"
    done
    exit 1
fi

cd "$LIB_PATH"

# Определяем, использовать Maven или Gradle
if [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
    echo "📦 Using Gradle to build shared library"
    
    if [ -f "gradlew" ]; then
        ./gradlew clean build publishToMavenLocal -x test
    else
        gradle clean build publishToMavenLocal -x test
    fi
    
    echo "✅ Shared library installed to Gradle cache"
    
elif [ -f "pom.xml" ]; then
    echo "📦 Using Maven to build shared library"
    
    if [ -f "mvnw" ]; then
        ./mvnw clean install -DskipTests
    else
        mvn clean install -DskipTests
    fi
    
    echo "✅ Shared library installed to Maven local repository"
    
else
    echo "❌ No build file found (build.gradle or pom.xml)"
    exit 1
fi

echo ""