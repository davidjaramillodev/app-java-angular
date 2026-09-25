# Guía del frontend

El frontend es una aplicación Angular de una sola página. Presenta el portal bancario, recoge las acciones del usuario y llama a la API pública del gateway. No se conecta directamente a las bases de datos ni decide permisos de seguridad.

## Carpetas principales

- `frontend/src/app/views/loan-portal.component.html`: lo que se muestra en pantalla: login, panel del usuario y panel del administrador.
- `frontend/src/app/views/loan-portal.component.css`: estilos de esas pantallas.
- `frontend/src/app/views/loan-portal.component.ts`: estado de la pantalla, validación de formularios y respuesta a eventos como entrar, solicitar un préstamo o decidirlo.
- `frontend/src/app/models/`: tipos TypeScript compartidos, por ejemplo `LoanApplication`, `LoanStatus` y `AccountRole`.
- `frontend/src/app/services/`: comunicación HTTP con el backend y envío del token de sesión.
- `frontend/src/app/app.config.ts`: configuración global de Angular; registra el interceptor HTTP.
- `frontend/src/proxy.conf.json`: en desarrollo, reenvía `/api/**` desde el puerto 4200 al gateway del puerto 8080.

## Qué ocurre al iniciar sesión

1. El formulario valida que el correo tenga formato válido y que se haya escrito una contraseña.
2. `AuthService` envía `POST /api/v1/auth/login`.
3. El backend responde con un token, el nombre y el rol.
4. `AuthService` conserva esos datos en señales de Angular. El token queda en memoria, no en `localStorage`.
5. `auth.interceptor.ts` agrega `Authorization: Bearer <token>` a las llamadas HTTP posteriores.
6. El componente usa el rol para mostrar la pantalla correspondiente y pide las solicitudes al servicio adecuado.

Al recargar el navegador se pierde la sesión y hay que volver a iniciar sesión. Es intencional para este ejemplo; no se persiste el token en el navegador.

## Pantallas y operaciones

**Usuario (`USER`)**

- Ve sus propias solicitudes.
- Puede solicitar un préstamo con importe mínimo de 100, plazo de 3 a 360 meses y una finalidad obligatoria de hasta 300 caracteres.
- Al enviar, Loan Service crea la solicitud en estado `PENDING` y el panel vuelve a cargar la lista.

**Administrador (`ADMIN`)**

- Ve todas las solicitudes y el total pendiente.
- Puede aprobar o rechazar las que siguen en estado `PENDING`.
- Después de guardar una decisión, la bandeja se actualiza.

La condición de rol en Angular solo controla qué interfaz se presenta. La autorización real también está en el gateway y en Loan Service; no se debe confiar en una comprobación visual del frontend.

## Añadir o cambiar una operación

1. Define o modifica el tipo de datos en `models/`.
2. Añade la llamada HTTP en el servicio correspondiente dentro de `services/`.
3. Llama a ese servicio desde el componente de vista y representa estados de carga, éxito y error en el HTML.
4. Si la operación requiere autorización, implementa también la regla en el backend. No basta con ocultar un botón.

Para cambiar texto o estructura visual, trabaja en el HTML. Para colores y distribución, trabaja en el CSS. Mantén las llamadas de red fuera de la plantilla.

## Comandos

Desde `frontend/`:

```powershell
npm.cmd install
npm.cmd start
npm.cmd run build
```

La aplicación local queda en `http://localhost:4200`. El build valida TypeScript y genera los archivos estáticos en `dist/`.