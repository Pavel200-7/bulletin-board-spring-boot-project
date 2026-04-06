#!/bin/bash
set -e

echo "========================================="
echo "🔐 Docker Hub Authentication"
echo "========================================="

if [ -n "$DOCKER_USERNAME" ] && [ -n "$DOCKER_PASSWORD" ]; then
    echo "Logging in to Docker Hub as: $DOCKER_USERNAME"
    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
    
    if [ $? -eq 0 ]; then
        echo "✅ Successfully logged in to Docker Hub"
    else
        echo "❌ Failed to login to Docker Hub"
        exit 1
    fi
else
    echo "⚠️  WARNING: DOCKER_USERNAME or DOCKER_PASSWORD not set"
    echo "Skipping Docker Hub login (will work only with public images)"
fi

echo ""