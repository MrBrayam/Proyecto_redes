## Plan: Flujo de viajes, conductor y administración

Objetivo: completar el flujo de solicitud de transporte para el rol PASAJERO, crear un dashboard post-login para CONDUCTOR con mapa, datos personales y cola de solicitudes, y ampliar el panel ADMIN para gestionar usuarios y reportes diarios, mensuales y anuales.

**Steps**
1. Definir el flujo base de viaje y sus estados en backend, reutilizando el rol PASAJERO existente y el esquema de `viajes` ya presente en `db/redes_schema.sql`.
2. Crear o completar la capa de dominio para solicitudes de viaje: entidad/modelo, DTOs, repositorio y servicio para registrar, listar, aceptar, rechazar y cerrar viajes. Este paso depende de 1.
3. Exponer endpoints REST y, si hace falta, WebSocket/STOMP para: crear solicitud desde pasajero, notificar nuevas solicitudes a conductores, mostrar ubicación del pasajero y actualizar estados en tiempo real. Este paso depende de 2.
4. Ajustar el dashboard de pasajero para que la solicitud quede asociada a una sesión/token, muestre estado real y continúe usando Google Maps con la API key ya configurada. Este paso depende de 3.
5. Crear el dashboard de conductor post-login con redirección desde `login-conductor.html`, validación de sesión/rol, mapa activo, panel lateral con datos personales del conductor y lista de pasajeros/viajes pendientes con acciones de aceptar o rechazar. Este paso depende de 3.
6. Integrar la ubicación del pasajero y la del conductor en el dashboard de conductor, de forma que el conductor vea dónde se encuentra el pasajero seleccionado y el backend pueda emitir actualizaciones de posición. Este paso depende de 3 y 5.
7. Ampliar el admin backend para administrar clientes/pasajeros, conductores y administradores desde `AdminController` y `AdminService`, incluyendo cambios de rol, alta/baja/edición y sincronización de perfiles. Este paso depende de 1.
8. Implementar reportes de día/mes/año en backend, agregando consultas agregadas sobre viajes, usuarios y estados; luego exponerlos por API para alimentar el dashboard admin. Este paso depende de 7.
9. Rediseñar el dashboard admin para mostrar secciones separadas de clientes, pasajeros, conductores, administradores y reportes con gráficos y exportación. Este paso depende de 8.
10. Validar el flujo completo con compilación, pruebas de integración o smoke tests y verificación manual de los tres roles: pasajero solicita, conductor recibe/acepta y admin gestiona/reporta.

**Relevant files**
- `c:/xampp/htdocs/Proyecto_redes/src/main/resources/static/pasajero/dashboard.html` — UI de solicitud de viaje y carga de Maps.
- `c:/xampp/htdocs/Proyecto_redes/src/main/resources/static/pasajero/pasajero.js` — lógica de mapa, envío de solicitud y WebSocket.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/controller/PasajeroController.java` — CRUD de pasajeros existente.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/service/PasajeroService.java` — lógica de alta/edición/borrado del pasajero.
- `c:/xampp/htdocs/Proyecto_redes/src/main/resources/static/auth/login-conductor.html` — login del conductor, punto de entrada al nuevo dashboard.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/controller/ConductorController.java` — CRUD administrativo de conductores.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/service/ConductorService.java` — lógica de negocio de conductores.
- `c:/xampp/htdocs/Proyecto_redes/src/main/resources/static/admin/dashboard.html` — dashboard actual del admin a ampliar.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/controller/AdminController.java` — CRUD de usuarios y base para gestión administrativa.
- `c:/xampp/htdocs/Proyecto_redes/src/main/java/api/proyecto/redes/service/AdminService.java` — sincronización de usuarios/roles y creación de perfiles admin.
- `c:/xampp/htdocs/Proyecto_redes/db/redes_schema.sql` — tablas `usuarios`, `pasajeros`, `conductores`, `viajes` y `calificaciones` para apoyar los reportes.
- `c:/xampp/htdocs/Proyecto_redes/src/main/resources/application.properties` — configuración de Maps y entorno.

**Verification**
1. Compilar el proyecto con Maven después de cada bloque principal de cambios.
2. Probar el login de pasajero, la creación de una solicitud y su persistencia/visualización en el backend.
3. Probar el login de conductor, la carga del mapa, la recepción de solicitudes y la acción aceptar/rechazar.
4. Probar el login de admin y verificar listados, cambios de rol y reportes por rango temporal.
5. Verificar manualmente que la API key de Google Maps se carga sin depender del entorno del terminal.

**Decisions**
- Se reutiliza el rol PASAJERO actual; no se crea un rol CLIENTE nuevo.
- El conductor verá solicitudes y decidirá manualmente aceptar o rechazar.
- El dashboard del conductor debe incluir el mapa y la ubicación del pasajero seleccionado.
- Los reportes del admin incluirán gráficos y exportación además de totales y tablas.
- El alcance no incluye móvil nativo ni IA nueva; se concentra en backend + dashboards web actuales.

**Further Considerations**
1. Definir si los viajes requieren estados extra como `ASIGNADO`, `EN_CAMINO`, `FINALIZADO` y `CANCELADO`, o si basta con un conjunto más simple.
2. Confirmar si la ubicación en tiempo real del conductor debe persistir en base de datos o solo circular por WebSocket.
3. Decidir si los reportes del admin se exportan en PDF, Excel o ambos.
