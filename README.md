# API Transportes

API REST desarrollada como solución a una prueba técnica Backend Senior Java utilizando **Java 17, Spring Boot y PostgreSQL**.

La aplicación implementa la gestión de órdenes de transporte y conductores mediante una arquitectura basada en microservicios, incluyendo autenticación JWT, persistencia, documentación OpenAPI/Swagger, pruebas unitarias y contenerización con Docker.

---

## Arquitectura

El proyecto está dividido en dos microservicios independientes:

### Order Service

Responsable de:

- Crear y consultar órdenes.
- Filtrar órdenes.
- Gestionar cambios de estado.
- Asignar conductores a órdenes.
- Validar conductores mediante comunicación HTTP con `driver-service`.
- Adjuntar archivos PDF e imágenes a las asignaciones.
- Generar y validar tokens JWT.

Puerto:

```text
8082
```

### Driver Service

Responsable de:

- Crear conductores.
- Consultar conductores activos.
- Consultar conductores por identificador.
- Validar JWT generado por `order-service`.

Puerto:

```text
8081
```

### Comunicación entre servicios

`order-service` consulta `driver-service` mediante HTTP para verificar que el conductor exista y se encuentre activo antes de realizar una asignación.

El JWT recibido por `order-service` se propaga hacia `driver-service` durante esta comunicación.

Cada microservicio mantiene su propia base de datos:

```text
PostgreSQL
├── order_db
└── driver_db
```

No existen relaciones mediante claves foráneas entre bases de datos. El `driverId` almacenado en una asignación funciona como referencia lógica al conductor administrado por `driver-service`.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (JJWT)
- PostgreSQL
- Flyway
- MapStruct
- Bean Validation
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Docker
- Docker Compose
- Maven

---

## Estructura del proyecto

```text
api-transportes/
├── driver-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── order-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker/
│   └── postgres/
│       └── init.sql
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Requisitos

Para ejecutar la solución completa únicamente se requiere:

- Docker
- Docker Compose

Para desarrollo local también se recomienda:

- Java 17
- Maven
- Git

---

## Configuración

Crear un archivo `.env` en la raíz del proyecto tomando como referencia `.env.example`.

Ejemplo:

```dotenv
POSTGRES_USER=transport_user
POSTGRES_PASSWORD=change_me
POSTGRES_HOST_PORT=5433

DRIVER_DB=driver_db
ORDER_DB=order_db

JWT_SECRET=replace_with_base64_256_bit_secret

APP_USERNAME=admin
APP_PASSWORD=replace_with_bcrypt_hash
```

> El archivo `.env` no debe almacenarse en el repositorio debido a que contiene credenciales y secretos.

`JWT_SECRET` debe ser un valor Base64 válido y debe ser compartido por ambos microservicios.

`APP_PASSWORD` debe contener el hash BCrypt correspondiente a la contraseña utilizada para autenticarse.

---

## Compilar los microservicios

Desde `driver-service`:

```bash
./mvnw clean package
```

En Windows:

```powershell
.\mvnw.cmd clean package
```

Después, desde `order-service`:

```bash
./mvnw clean package
```

En Windows:

```powershell
.\mvnw.cmd clean package
```

---

## Ejecutar con Docker

Desde la raíz del proyecto:

```bash
docker compose up --build -d
```

Comprobar los contenedores:

```bash
docker ps
```

Deben estar disponibles:

```text
transportes-postgres
driver-service
order-service
```

Para detener la aplicación:

```bash
docker compose down
```

Los datos de PostgreSQL y los archivos cargados utilizan volúmenes Docker, por lo que se conservan al detener o reiniciar los contenedores.

Para eliminar también los volúmenes:

```bash
docker compose down -v
```

> Este último comando elimina los datos persistidos del entorno Docker.

---

## Swagger / OpenAPI

### Order Service

```text
http://localhost:8082/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8082/v3/api-docs
```

### Driver Service

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8081/v3/api-docs
```

---

## Autenticación

La autenticación se realiza mediante JWT.

`order-service` expone el endpoint de login:

```http
POST /auth/login
```

Una autenticación correcta devuelve un token:

```json
{
  "token": "<JWT>",
  "type": "Bearer"
}
```

Las demás operaciones protegidas requieren:

```http
Authorization: Bearer <JWT>
```

En Swagger UI puede utilizarse el botón **Authorize** e introducir el token generado por `/auth/login`.

El mismo JWT es reconocido por ambos microservicios.

---

## Endpoints principales

### Order Service

```text
POST  /auth/login

POST  /api/orders
GET   /api/orders
GET   /api/orders/{id}
PATCH /api/orders/{id}/status

POST  /api/orders/{orderId}/assignments

PATCH /api/orders/{orderId}/assignments/pdf
PATCH /api/orders/{orderId}/assignments/image
```

El listado de órdenes permite aplicar filtros por:

- estado
- origen
- destino
- rango de fecha de creación

### Driver Service

```text
POST /api/drivers
GET  /api/drivers
GET  /api/drivers/{id}
```

---

## Estados de una orden

Los estados disponibles son:

```text
CREATED
IN_TRANSIT
DELIVERED
CANCELLED
```

Las transiciones permitidas son:

```text
CREATED ──────> IN_TRANSIT ──────> DELIVERED
   │
   └──────────> CANCELLED
```

Una orden entregada o cancelada se considera un estado final.

---

## Asignación de conductores

Para asignar un conductor a una orden deben cumplirse las siguientes condiciones:

- La orden debe existir.
- La orden debe encontrarse en estado `CREATED`.
- La orden no debe tener una asignación previa.
- El conductor debe existir.
- El conductor debe estar activo.

`order-service` consulta `driver-service` para validar al conductor antes de persistir la asignación.

---

## Archivos

Una asignación permite adjuntar:

- Un archivo PDF.
- Una imagen PNG/JPG.

Los archivos son almacenados por `order-service`.

En Docker se utiliza:

```text
/app/uploads
```

El directorio está respaldado mediante un volumen Docker para conservar los archivos después de reiniciar los contenedores.

---

## Migraciones de base de datos

El esquema es administrado mediante Flyway.

Cada microservicio mantiene sus propias migraciones dentro de:

```text
src/main/resources/db/migration
```

Las migraciones se ejecutan automáticamente al iniciar cada servicio.

---

## Pruebas

Las pruebas utilizan:

- JUnit 5
- Mockito
- Spring MockMvc

Para ejecutar las pruebas:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

También pueden ejecutarse durante el empaquetado:

```bash
./mvnw clean package
```

---

## Manejo de errores

La API implementa manejo centralizado de excepciones mediante `@ControllerAdvice`.

Entre los escenarios controlados se encuentran:

- Recursos inexistentes.
- Datos de entrada inválidos.
- Transiciones de estado no permitidas.
- Órdenes previamente asignadas.
- Conductores inexistentes o inactivos.
- Fallos de comunicación entre microservicios.
- Solicitudes no autenticadas.
- Archivos inválidos.

Las respuestas utilizan códigos HTTP acordes al tipo de error.

---

## Flujo de prueba recomendado

1. Iniciar la infraestructura con Docker Compose.
2. Autenticarse mediante `POST /auth/login`.
3. Copiar el JWT obtenido.
4. Autorizar Swagger utilizando el JWT.
5. Crear un conductor activo en `driver-service`.
6. Crear una orden en `order-service`.
7. Asignar el conductor a la orden.
8. Adjuntar un PDF a la asignación.
9. Adjuntar una imagen PNG/JPG.
10. Consultar nuevamente la orden y sus operaciones relacionadas.

Este flujo permite comprobar la autenticación, persistencia, comunicación entre microservicios y almacenamiento de archivos.

---

## Decisiones de diseño

La solución separa los dominios de órdenes y conductores en dos microservicios independientes.

Cada servicio es propietario de sus datos y no accede directamente a las tablas del otro servicio.

La comunicación entre servicios se realiza mediante HTTP y las referencias entre dominios se manejan mediante identificadores UUID.

Se utilizó Flyway para mantener el esquema de base de datos versionado y reproducible.

La autenticación se mantiene intencionalmente simple mediante JWT compartido entre servicios, adecuada al alcance de la prueba técnica.

Docker Compose permite levantar PostgreSQL y ambos microservicios como un único entorno reproducible.

---

## Autor

Daniel Ramírez Montoya