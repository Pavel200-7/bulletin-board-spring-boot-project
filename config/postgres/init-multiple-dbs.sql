CREATE DATABASE keycloak_db;
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO dbuser;

CREATE DATABASE bulletin_db;
GRANT ALL PRIVILEGES ON DATABASE bulletin_db TO dbuser;

-- CREATE DATABASE imageservice_db;
-- GRANT ALL PRIVILEGES ON DATABASE imageservice_db TO dbuser;

-- CREATE DATABASE notificationservice_db;
-- GRANT ALL PRIVILEGES ON DATABASE notificationservice_db TO dbuser;

-- \c imageservice_db

-- CREATE TABLE IF NOT EXISTS blobs (
--     id VARCHAR(255) PRIMARY KEY,
--     content OID NOT NULL
-- );

-- GRANT ALL PRIVILEGES ON TABLE blobs TO dbuser;