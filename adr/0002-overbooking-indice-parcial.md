# ADR 0002: Evitar overbooking con un índice único parcial

## Contexto

Dos peticiones simultáneas pueden ver la misma mesa libre. Comprobar y luego insertar en la aplicación deja una ventana en la que ambas reservan.

## Alternativas

- **`SELECT ... FOR UPDATE`:** funciona, pero obliga a bloquear filas, mantener una transacción alrededor de la asignación y cuidar el orden de bloqueo para evitar interbloqueos.
- **Bloqueo optimista con versión:** protege contra modificar la misma fila, pero aquí se insertan filas nuevas, así que no hay versión compartida que comparar.
- **Índice único parcial:** PostgreSQL rechaza una segunda reserva activa para la misma mesa, fecha y turno.

## Decisión

Usar el índice `uq_reservation_active_slot`, limitado a estados `PENDING` y `CONFIRMED`, e insertar con `ON CONFLICT DO NOTHING`.

La inserción va por `JdbcClient` porque JPA no expresa esa cláusula. Además, una violación de unicidad normal abortaría la transacción de PostgreSQL y complicaría probar la siguiente mesa.

El bloqueo optimista con `@Version` sí se usa en un caso distinto: dos cambios de estado sobre una misma reserva.

## Consecuencias

La garantía se cumple para cualquier cliente de la base de datos, y cancelar libera la mesa sin pasos extra.

El coste es que una regla de negocio vive en el esquema. Por eso `SchemaIT` la prueba directamente y `OverbookingConcurrencyIT` lo hace con peticiones simultáneas.
