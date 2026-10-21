# Esquema implementado
```mermaid
erDiagram
 USUARIOS {
  BIGINT id PK
  VARCHAR email UK
  VARCHAR password_hash
  VARCHAR rol
 }
 SERVICIOS {
  BIGINT id PK
  VARCHAR nombre
  VARCHAR nombre_clave UK
  DECIMAL precio
  INT duracion_minutos
 }
```
Dos tablas independientes: no existe relación FK en este primer sprint. El diagrama SVG se puede abrir sin herramientas adicionales en un navegador.
