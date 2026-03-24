FROM minio/minio:latest

# Устанавливаем mc (MinIO Client)
RUN curl -o /usr/bin/mc https://dl.min.io/client/mc/release/linux-amd64/mc && \
    chmod +x /usr/bin/mc

# Копируем скрипт инициализации
COPY init.sh /docker-entrypoint-initdb.d/init.sh
RUN chmod +x /docker-entrypoint-initdb.d/init.sh

# Запускаем MinIO и скрипт инициализации
CMD ["server", "--console-address", ":9002", "/data"]