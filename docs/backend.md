# Guía del backend

El backend tiene dos microservicios Spring Boot: `auth-service` es dueño de las cuentas y `loan-service` es dueño de las solicitudes de préstamo. Angular solo llega a ellos a través de Spring Cloud Gateway.

## Organización MVC por servicio

En cada microservicio Java las responsabilidades se separan por paquete:

- `controller/`: adapta HTTP a llamadas de servicio. Define rutas, códigos de respuesta y validación de entrada; no contiene consultas directas a JPA ni reglas principales.
- `service/`: ejecuta los casos de uso y reglas del negocio. Aquí se decide cómo autenticar, crear un préstamo o resolver una decisión.
- `model/`: entidades persistidas, enumeraciones y objetos de entrada/salida (DTOs).
- `repository/`: interfaces Spring Data JPA para consultar y guardar datos.
- `config/`: seguridad, carga de datos de demostración y configuración del servicio.

Angular es la vista: sus controladores de presentación son componentes; sus plantillas y estilos viven en `frontend/src/app/views/`.

## Auth Service

Auth Service escucha en el puerto `8082`.

- `POST /api/v1/auth/login`: recibe `username` y `password`; es la única ruta de autenticación que acepta llamadas anónimas.
- `GET /api/v1/auth/me`: devuelve el usuario autenticado y sus roles; requiere Bearer token.

Durante el login, `DatabaseUserDetailsService` busca el correo sin distinguir mayúsculas, lee el hash BCrypt y lo entrega a Spring Security. `AuthService` autentica las credenciales y emite un JWT de una hora. El token incluye el correo como `subject` y roles como `ROLE_USER` o `ROLE_ADMIN`. La respuesta de login también incluye `role` sin el prefijo `ROLE_`, para que Angular elija el panel.

`DemoUserSeeder` crea estos usuarios solo si no existen:

- `usuario@test.com`, contraseña `123`, rol `USER`.
- `admin@test.com`, contraseña `123`, rol `ADMIN`.

En la base solo se guarda el hash, no la contraseña literal.

## Loan Service

Loan Service escucha en el puerto `8081`. La entidad `Loan` representa una solicitud con solicitante, importe, plazo, finalidad, fecha y estado.

- `GET /api/v1/loans/my`: devuelve las solicitudes del usuario del token.
- `POST /api/v1/loans`: crea una solicitud para el usuario del token; el cliente no puede elegir el correo del solicitante.
- `GET /api/v1/admin/loans`: devuelve todas las solicitudes para el administrador.
- `PATCH /api/v1/admin/loans/{id}/decision`: aprueba o rechaza una solicitud pendiente.

Las decisiones válidas son `APPROVED` y `REJECTED`. Una solicitud inexistente responde `404`; una solicitud que ya no esté pendiente responde `409`. El servidor vuelve a validar importes, plazos y finalidad: los validadores de Angular no sustituyen esta validación.

## Seguridad y gateway

El gateway escucha en `8080` y enruta `/api/v1/auth/**` a Auth Service y las rutas `/api/v1/loans/**` y `/api/v1/admin/loans/**` a Loan Service. Requiere JWT para rutas protegidas. USER no puede acceder al panel admin; Loan Service verifica también los roles, así que llamar directamente al microservicio no evita la autorización.

Los tokens usan HS256. Los tres componentes deben compartir el mismo `JWT_SECRET` y el mismo emisor configurado como `app.security.jwt-issuer`. La clave predeterminada del repositorio es solo para desarrollo; producción requiere un secreto externo y, preferiblemente, OIDC o firma asimétrica.

## Persistencia

- Auth Service solo escribe usuarios en su base.
- Loan Service solo escribe solicitudes en su base.
- En desarrollo, el perfil `local` usa H2 en archivos bajo `services/auth-service/data/` y `services/loan-service/data/`.
- Para otro entorno, cada servicio acepta su URL y credenciales mediante `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
- `ddl-auto: update` y los usuarios de demostración son para desarrollo. Antes de producción deben sustituirse por migraciones y gestión real de cuentas.

## Pruebas unitarias

Los tests usan JUnit 5 y Mockito; simulan las dependencias y no necesitan PostgreSQL ni H2:

- `AuthServiceTest`: token/rol emitido, rechazo de credenciales y lectura del perfil desde JWT.
- `LoanServiceTest`: consulta por solicitante, creación, listado general, decisión válida, conflicto y préstamo inexistente.

Desde la raíz:

```powershell
mvn -f services/auth-service/pom.xml test
mvn -f services/loan-service/pom.xml test
```

Para cambiar una regla del negocio, modifica el servicio correspondiente y añade el caso esperado al test de esa capa. Para cambiar rutas o el formato JSON, actualiza también el contrato OpenAPI en `contracts/`.