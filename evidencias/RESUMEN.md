# Resumen de ejecución real

Fecha UTC: 2026-10-21T02:58:56+00:00.
Entorno: Linux, Zulu OpenJDK 21.0.8, Maven 3.9.9, Spring Boot 3.5.7. IntelliJ no se ejecutó en este entorno; los tests son Maven/JUnit y la guía está orientada a IntelliJ IDEA 2025.3.1.

- BUILD SUCCESS en clean verify.
- 37 pruebas unitarias + 24 pruebas de integración/API = 61 ejecuciones JUnit aprobadas.
- Cero fallos, errores u omisiones.
- Cobertura medida de líneas: 97.5% (no significa cobertura completa de escenarios).
- Flujo adicional sobre servidor HTTP real: 11 pasos aprobados.
- Persistencia H2 en archivo comprobada al detener y reiniciar el JAR.
- Carga local: 100 GET autenticados, 5 conexiones concurrentes, 0 errores; p95 180.41 ms, media 38.26 ms. Son métricas de este entorno y corrida, no una garantía de producción.

No sumar la carga a los 61 tests JUnit. Humo, regresión, aceptación técnica y seguridad reutilizan casos identificados de la suite: no se cuentan como suites duplicadas.
MySQL no se ejecutó: se entrega script y perfil opcional, sin afirmar integración verificada con ese motor.
Accesibilidad y usabilidad: checklist manual propuesto, pendiente para un futuro cliente visual.
GitHub: publicación pendiente por parte del usuario; seguir docs/GITHUB_PASO_A_PASO.md.

El reporte puede registrar un error SQL de restricción única: es provocado intencionalmente por la prueba que exige rechazar un nombre duplicado. El resultado de esa prueba es aprobado; revisar los contadores finales para distinguirlo de un fallo del test.
