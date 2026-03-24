#!/bin/sh

echo "Waiting for MinIO to be ready..."
sleep 10

# Добавляем alias для minio
mc alias set myminio http://localhost:9000 minioadmin password

# Создаем bucket (если не существует)
mc mb myminio/bulletins --ignore-existing

# Делаем bucket публичным
mc anonymous set public myminio/bulletins

# Настраиваем CORS для bucket
mc anonymous set-upload myminio/bulletins

echo "MinIO initialization complete!"