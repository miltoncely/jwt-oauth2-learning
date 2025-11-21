# JWT OAuth2 Learning Project

Proyecto educativo completo de autenticación y autorización con JWT (RS256), Spring Boot WebFlux, H2, Redis y RBAC.

## 🏗️ Arquitectura

    KG[Key Generator] -->|Private Key| AS[Auth Service]
    KG -->|Public Key| AS
    KG -->|Public Key| RS[Resource Service]
    
    User -->|1. Login| AS
    AS -->|2. Validate H2| DB[(H2 Database)]
    AS -->|3. Sign JWT| AS
    AS -->|4. Store Active Token| Redis[(Redis Whitelist)]
    AS -->|5. Return JWT| User
    
    User -->|6. Request + JWT| RS
    RS -->|7. Check Whitelist| Redis
    RS -->|8. Verify Signature| RS
    RS -->|9. Check RBAC| RS
    RS -->|10. Return Data| User
    
    User -->|11. Logout| AS
    AS -->|12. Delete Token| Redis
```

## 📦 Módulos

### 1. key-generator
Genera claves RSA 2048-bit y las distribuye a los servicios.

### 2. auth-service
- **Base de datos**: H2 in-memory con R2DBC
- **Usuarios por defecto**:
  - `admin` / `123456` (Roles: ADMIN, USER)
  - `user` / `password` (Roles: USER)
- **Endpoints**:
  - `POST /oauth/token` - Obtener JWT
  - `POST /oauth/revoke` - Revocar token (logout)

### 3. resource-service
- **Endpoints públicos**:
  - `GET /public/info`
- **Endpoints protegidos** (requieren JWT):
  - `GET /api/secure-data` - Cualquier usuario autenticado
  - `GET /api/admin` - Solo ADMIN
  - `GET /api/user` - Solo USER (o ADMIN)
  - `GET /api/users/{username}` - Solo el usuario dueño (granular auth)

## 🚀 Guía de Ejecución

### Prerrequisitos
- Java 21
- Docker (para Redis)

### Paso 1: Generar Claves
```bash
./gradlew :key-generator:compileJava
java -cp key-generator/build/classes/java/main com.learning.keygenerator.Main
```

### Paso 2: Iniciar Redis
```bash
docker run -d --name jwt-redis -p 6379:6379 redis
```

### Paso 3: Iniciar Auth Service
```bash
./gradlew :auth-service:bootRun
```
*Corre en puerto 8080*

### Paso 4: Iniciar Resource Service
```bash
./gradlew :resource-service:bootRun
```
*Corre en puerto 8081*

## 🧪 Pruebas

### Opción A: Postman
Importa `postman_collection.json` y ejecuta los escenarios:
- **Full Lifecycle**: Login → Access → Revoke → Access Fail
- **Granular Auth**: User accede solo a sus datos

### Opción B: cURL

**1. Obtener Token (Admin)**
```bash
curl -X POST "http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456"
```

**2. Acceder a Endpoint Protegido**
```bash
TOKEN="<tu_token>"
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/admin
```

**3. Revocar Token (Logout)**
```bash
curl -X POST "http://localhost:8080/oauth/revoke?token=$TOKEN"
```

## 🔐 Estrategia de Validación: Redis Whitelist

Este proyecto implementa una **lista blanca (whitelist)** de tokens en Redis:

### ¿Cómo funciona?

| Evento | Acción en Redis | Estado del Token |
|--------|----------------|------------------|
| **Login exitoso** | `SET token username TTL=1h` | ✅ Token activo |
| **Request al resource-service** | `EXISTS token` → true | ✅ Válido |
| **Logout/Revoke** | `DELETE token` | ❌ Revocado |
| **Request después de logout** | `EXISTS token` → false | ❌ Rechazado |
| **Token expira (1h)** | Redis elimina automáticamente | ❌ Expirado |

### Ventajas de Whitelist vs Blacklist

| Aspecto | Whitelist ✅ (Implementado) | Blacklist |
|---------|---------------------------|-----------|
| **Almacenamiento** | Solo tokens activos | Solo tokens revocados |
| **Seguridad** | Fail-closed (Redis caído = rechaza todo) | Fail-open (Redis caído = acepta todo) |
| **Control** | Control total sobre tokens válidos | Requiere logout explícito |
| **Espacio** | Crece con usuarios activos | Crece con revocaciones |

### Implementación Técnica

**Auth Service** (`JwtService.java`):
```java
// Al generar token
activeTokensCache.opsForValue().set(token, username, Duration.ofHours(1));

// Al revocar token
activeTokensCache.opsForValue().delete(token);
```

**Resource Service** (`RedisTokenValidator.java`):
```java
// Validación
public Mono<Boolean> isTokenActive(String token) {
    return activeTokensCache.hasKey(token)
        .defaultIfEmpty(false); // Fail-closed
}
```

## 🔍 Monitoreo de Redis

### Conectarse al CLI de Redis
```bash
docker exec -it jwt-redis redis-cli
```

### Comandos Útiles para Debugging

#### Ver todos los tokens activos
```bash
KEYS *
```

#### Contar tokens activos
```bash
DBSIZE
```

#### Ver tiempo de vida restante de un token
```bash
TTL "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Ver el valor asociado a un token
```bash
GET "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Monitorear operaciones en tiempo real
```bash
MONITOR
```

#### Ver información del servidor
```bash
INFO
```

#### Ver uso de memoria
```bash
INFO memory
```

#### Ver estadísticas de comandos
```bash
INFO stats
```

### Comandos de Limpieza (⚠️ Usar con cuidado)

#### Eliminar un token específico
```bash
DEL "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Cerrar todas las sesiones (eliminar todos los tokens)
```bash
FLUSHALL
```
> ⚠️ **CUIDADO**: Esto revoca TODOS los tokens activos

## 🎓 Conceptos Implementados
- **RS256**: Firma asimétrica (privada firma, pública verifica)
- **H2 + R2DBC**: Base de datos reactiva in-memory
- **RBAC**: Control de acceso basado en roles
- **Granular Authorization**: Control a nivel de recurso individual
- **Token Whitelist (Redis)**: Solo tokens activos almacenados; logout elimina el token
- **WebFlux Security**: Cadena de seguridad reactiva
- **DDD Light**: Organización en capas (api, config, domain, security, persistence)
- **Fail-Closed Security**: Si Redis no está disponible, se rechazan todos los tokens

## 📁 Estructura del Proyecto

```
jwt-oauth2-learning/
├── key-generator/          # Generador de claves RSA
├── auth-service/           # Servicio de autenticación
│   └── src/main/java/com/learning/auth/
│       ├── api/            # Controllers
│       ├── config/         # Configuraciones (Redis)
│       ├── domain/         # Modelos de negocio (User)
│       ├── security/       # JWT, Security Config
│       └── persistence/    # Repositories, DB Init
└── resource-service/       # Servicio de recursos
    └── src/main/java/com/learning/resource/
        ├── api/            # Controllers
        ├── config/         # Configuraciones (Redis)
        └── security/       # JWT Validation, Security Config
```

## 📚 Documentación Adicional
- [Auth Service README](auth-service/README.md)
- [Resource Service README](resource-service/README.md)
- [Key Generator README](key-generator/README.md)
