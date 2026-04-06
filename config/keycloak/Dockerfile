FROM quay.io/keycloak/keycloak:26.5.2

# Создаем директорию для провайдеров
USER root
RUN mkdir -p /opt/keycloak/providers

# Копируем собранный SPI провайдер
COPY spi-providers/*.jar /opt/keycloak/providers/

# Возвращаемся к пользователю keycloak
USER keycloak

# Команда по умолчанию (может быть переопределена)
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]