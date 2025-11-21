-- Cliente (client_id: client, client_secret: secret)
INSERT INTO oauth_clients (client_id, client_secret, client_name, allowed_scopes, grant_types, redirect_uris, access_token_validity, refresh_token_validity)
VALUES ('client', 'secret', 'Test Client', 'read write', 'client_credentials,password,refresh_token', 'http://localhost:8080/callback', 3600, 604800);

-- Usuario (username: user, password: password)
-- La contraseña es "password" hasheada con BCrypt
INSERT INTO users (username, email, password, full_name, roles, enabled, locked)
VALUES ('user', 'user@example.com', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.Z.pKo8ULMCw2', 'Test User', 'USER', true, false);