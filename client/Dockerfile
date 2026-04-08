# СТЕЙДЖ 1: Сборка
FROM node:22-alpine AS builder
WORKDIR /app

ARG VITE_API_URL
ARG VITE_KEYCLOAK_URL
ARG VITE_KEYCLOAK_CLIENT_ID
ARG VITE_APP_URL
ARG VITE_MINIO_URL
ARG VITE_MINIO_BUCKET

ENV VITE_API_URL=$VITE_API_URL
ENV VITE_KEYCLOAK_URL=$VITE_KEYCLOAK_URL
ENV VITE_KEYCLOAK_CLIENT_ID=$VITE_KEYCLOAK_CLIENT_ID
ENV VITE_APP_URL=$VITE_APP_URL
ENV VITE_MINIO_URL=$VITE_MINIO_URL
ENV VITE_MINIO_BUCKET=$VITE_MINIO_BUCKET

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# СТЕЙДЖ 2: Запуск (используем serve)
FROM node:20-alpine

# Устанавливаем serve глобально
RUN npm install -g serve

WORKDIR /app

COPY --from=builder /app/dist ./dist

RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 5173

# Healthcheck для serve
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:5173/ || exit 1

CMD ["serve", "-s", "dist", "-l", "5173"]