# Auth Server

Este módulo es el **Authorization Server** del sistema, encargado de la autenticación y emisión de tokens JWT (JSON Web Tokens).

## Responsabilidades

1.  **Autenticación de Usuarios**: Valida credenciales (username/password) contra la base de datos.
2.  **Autenticación de Clientes**: Valida credenciales de aplicaciones cliente (client_id/client_secret).
3.  **Emisión de Tokens**: Genera Access Tokens y Refresh Tokens firmados con RSA.
4.  **Gestión de Usuarios**: Proporciona endpoints para crear y administrar usuarios (rol ADMIN).

## Arquitectura

El proyecto sigue una **Arquitectura Hexagonal** (Ports & Adapters):

*   **Domain**: Contiene la lógica de negocio pura y modelos (`User`, `Client`). No tiene dependencias de frameworks externos.
*   **Application**: Contiene los Casos de Uso (`GenerateTokenUseCase`, `ClientCredentialsUseCase`) que orquestan la lógica de negocio.
*   **Infrastructure**: Implementaciones técnicas (Repositorios R2DBC, Seguridad Spring Security, Generación JWT).
*   **Presentation**: Controladores REST (`TokenController`, `UserController`).

## Endpoints Principales

### OAuth 2.0 / OpenID Connect

*   `POST /oauth/token`: Endpoint principal para obtener tokens. Soporta los siguientes `grant_type`:
    *   `password`: Para login de usuarios.
    *   `client_credentials`: Para autenticación máquina a máquina.
    *   `refresh_token`: Para renovar tokens expirados.

### Autenticación Simplificada

*   `POST /auth/login`: Alias amigable para el flujo `password`.

### Gestión de Usuarios (Admin)

*   `GET /api/users`: Listar usuarios.
*   `POST /api/users`: Crear usuario.
*   `PUT /api/users/{id}`: Actualizar usuario.
*   `PATCH /api/users/{id}/status`: Habilitar/Deshabilitar usuario.

## Configuración

La configuración principal reside en `application.yml`.

*   **Base de Datos**: H2 en memoria (por defecto).
*   **Seguridad**:
    *   Las claves RSA para firmar tokens se cargan desde `src/main/resources/keys/private_key.pem`.
    *   La clave pública debe ser distribuida a los Resource Servers para validar los tokens.

## Notas de Desarrollo

*   **Programación Reactiva**: Todo el módulo utiliza Project Reactor (`Mono`, `Flux`) y Spring WebFlux.
*   **Seguridad**: Se utiliza Spring Security WebFlux. CSRF está deshabilitado ya que es una API REST stateless.
