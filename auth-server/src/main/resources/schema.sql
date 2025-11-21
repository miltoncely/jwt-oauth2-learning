-- Schema para Authorization Server
-- Base de datos H2 (compatible con SQL estándar)

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    roles VARCHAR(255), -- Roles separados por comas (simplificado)
    enabled BOOLEAN DEFAULT TRUE,
    locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de clientes OAuth 2.0
CREATE TABLE IF NOT EXISTS oauth_clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    allowed_scopes VARCHAR(255), -- Scopes separados por espacios
    grant_types VARCHAR(255), -- Grant types separados por comas
    redirect_uris VARCHAR(500), -- URIs separadas por comas
    enabled BOOLEAN DEFAULT TRUE,
    access_token_validity INT DEFAULT 3600, -- Segundos
    refresh_token_validity INT DEFAULT 604800 -- Segundos (7 días)
);

-- Índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_oauth_clients_client_id ON oauth_clients(client_id);