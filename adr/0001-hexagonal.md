# ADR 0001: Arquitectura hexagonal

## Contexto

Las reglas de reserva (estados, plazos, asignación de mesa) son la parte con más valor y la que más debe poder probarse sin infraestructura.

## Decisión

El código se divide en tres capas:

- `domain`: modelos y reglas, sin Spring ni JPA.
- `application`: casos de uso y puertos.
- `adapter`: controladores HTTP y persistencia.

## Consecuencias

Las reglas se prueban con tests unitarios rápidos, y JPA no condiciona el modelo.

El coste es más código: entidades separadas de los modelos y mapeo manual. Para un CRUD simple sería excesivo; aquí se justifica por las reglas de negocio.
