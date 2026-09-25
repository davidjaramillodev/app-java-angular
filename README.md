# App Java + Angular

Aplicación de préstamos bancarios con Angular, Spring Cloud Gateway y microservicios Java. Auth Service y Loan Service tienen bases de datos independientes.

## Documentación

- [Guía del frontend](docs/frontend.md): pantallas, modelos, servicios Angular y flujo del token.
- [Guía del backend](docs/backend.md): MVC, endpoints, roles, persistencia y pruebas.
- [Arquitectura general](ARCHITECTURE.md): componentes y comunicación entre servicios.

## Ejecutar localmente

Requisitos: JDK 21, Maven 3.9 o superior y Node.js 22 LTS con npm. Auth Service y Loan Service usan bases H2 locales; no necesitas instalar PostgreSQL.

Abre cuatro terminales desde la raíz del repositorio:

Terminal 1, usuarios/auth:

```powershell
cd services/auth-service
$env:SPRING_PROFILES_ACTIVE = 'local'
mvn spring-boot:run
```

Terminal 2, préstamos:

```powershell
cd services/loan-service
$env:SPRING_PROFILES_ACTIVE = 'local'
mvn spring-boot:run
```

Terminal 3, gateway:

```powershell
mvn -f gateway/pom.xml spring-boot:run
```

Terminal 4, Angular:

```powershell
Set-Location frontend
npm.cmd install
npm.cmd start
```

Abre `http://localhost:4200` e inicia con una de estas cuentas:

- Usuario: `usuario@test.com` / `123`
- Administrador: `admin@test.com` / `123`

Los usuarios se guardan con contraseña BCrypt. El usuario crea y consulta sus préstamos; el administrador revisa, aprueba o rechaza solicitudes. H2 guarda los datos en `services/auth-service/data/` y `services/loan-service/data/`.

Las cuentas y la clave JWT predeterminadas son solo para desarrollo. En producción cambia las contraseñas, configura `JWT_SECRET` desde un gestor de secretos y migra a OIDC o firma asimétrica.

## Estado

- Implementado: login Angular, roles USER/ADMIN, Auth Service y Loan Service, solicitudes y decisiones de préstamos con H2 local.
- Pendiente antes de producción: integración OIDC o firma asimétrica, recuperación de contraseña, validación de solvencia, migraciones de esquema y observabilidad.

El entorno de desarrollo crea los dos usuarios y una solicitud pendiente de muestra. `ddl-auto: update` se usa únicamente para desarrollo; reemplazarlo por migraciones antes de producción.

## Pruebas unitarias

JUnit 5 y Mockito prueban los servicios sin levantar bases de datos:

```powershell
mvn -f services/auth-service/pom.xml test
mvn -f services/loan-service/pom.xml test
```