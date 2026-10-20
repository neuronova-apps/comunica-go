# Etapa 2 — extracción trazable

Fecha: 20 de octubre de 2026, America/Lima.

**ETAPA_2_EXTRACCION_COMPLETADA. Runtime independiente pendiente de Etapa 3.**

La carpeta Android definitiva sigue siendo `ComunicaGo`. La plantilla original
está versionada en `d010356`, sobre el commit destino `95f78ed`. Su estructura,
MainActivity, identidad, Gradle y recursos se conservan sin sustitución.

## Fuente y selección

Se extrajeron **102 archivos** desde el commit exacto
`3cd7cf2e984af5c028bfb6cb355dd023f14e09f9` de una copia bare aislada del bundle validado.
El bundle original conserva SHA-256
`f15c783d04ed0e1bfcd1f53072bc2977e7999e949a018ef315a7f894172684c5`.
No se usó como fuente el commit remoto anterior de RutinaGo.

- `migration/etapa2/source/`: 77 archivos; comunicación, dominio/datos/UI,
  infraestructura compartida, pruebas unitarias e instrumentadas, fixture PNG
  con créditos y decisiones curatoriales. Conservan sus rutas, packages y bytes.
- `migration/etapa2/reference-only/`: 25 archivos mixtos/configuración
  para extraer símbolos o comparar en Etapa 3. **No copiar archivos completos al
  runtime**: algunos aún importan Room, rutinas o preferencias del host.
- `EXTRACCION_ETAPA_2.json`: ruta original/destino, clase B/C/D, rol, tamaño,
  objeto Git y SHA-256 de cada archivo. Incluye hash de trabajo de la auditoría;
  la extracción canónica usa bytes del objeto del commit, no conversiones EOL.

Este staging versionado materializa la extracción sin sobrescribir la plantilla
ni mezclar la adaptación de Etapa 3. Está fuera de los source sets Gradle activos;
no es otro proyecto Android ni necesita un segundo checkout. La app activa aún
muestra la pantalla de la plantilla. Las pruebas extraídas requieren adaptación
y no se presentan como ejecutadas.

No se copiaron implementaciones Room, schemas, catálogo de rutinas,
CatalogContainer, RutinaGoApplication, MainActivity del origen, selector de módulos,
APK, caché de usuario, perfiles privados, llaves ni evidencia visual masiva.
La evidencia completa permanece disponible en el bundle y respaldos existentes.

## Banco y trazabilidad

El catálogo extraído conserva SHA-256
`666d24b453f29a360d5bd42547a5c57ff2256b4430622714a05bfbdb21c3d718`:
132 vocablos, 18 categorías, 119 asociaciones (116 IDs distintos), 13 SOLO_TEXTO.
Se contrastan los 132 pares con la auditoría. Estoy, Dame y Ruido siguen sin ID.
El JSON original de decisiones y las decisiones finales/complementarias se
conservan byte a byte junto al catálogo original anterior a la curación, que es
solo evidencia y no sustituye al catálogo curado.

No se consultó Sheets ni ARASAAC. No se copiaron datos de un dispositivo.

## Adaptaciones completas para la Etapa 3

`ADAPTACIONES_ETAPA_3.json` enumera los archivos mixtos y la acción concreta.
Además del cambio de package/imports a `com.neuronovaapps.comunicago`, se necesita:

1. Crear raíz y contenedor propios e integrar la fuente en los source sets activos.
   Sustituir Greeting solo después de comparar con el respaldo.
2. Separar registro de perfiles, apariencia y los cinco contratos de comunicación;
   crear DataStore independiente y conservar UUID, null/vacío y borrado coordinado.
3. Crear worker solo con allowlist, extraer la cola WorkManager sin scheduler de
   rutinas y conservar FileStore/Downloader, caché primero y fallback textual.
4. Extraer componentes Compose y atribución, sin RoutineCard ni buscador HTTP.
   Conservar alto contraste, escalas, créditos, idioma español y lifecycle TTS.
5. Integrar Inicio/Mi vocabulario/Configuración sin selector del host y ofrecer
   perfiles y accesibilidad propios. No importar datos privados automáticamente.
6. Añadir solo dependencias necesarias tras comparación: DataStore, WorkManager,
   coroutines, Gson, OkHttp y Coil; comprobar dependencias Compose/lifecycle usadas.
   Revisar INTERNET, queries TTS y backup. Conservar SDK/identidad de la plantilla.
   La diferencia core-ktx 1.19.1 (plantilla) / 1.19.0 (origen) queda sin modificar.
7. Adaptar pruebas migradas y fixtures. La compilación, APK y validación en celular
   corresponden a Etapa 4. No se declara aún autonomía funcional validada.

## Verificación reproducible

Ejecutar `python -B tools/verify_stage2.py` desde la raíz. Verifica los bytes,
objetos Git, cobertura del manifiesto, catálogo, los 132 pares y que el staging
no esté activado en Gradle. No requiere red ni instalación de RutinaGo.

La reconciliación está descrita en `RECONCILIACION_PLANTILLA.md`. El respaldo
externo conserva la plantilla completa, Git anidado, bundle destino, fuente
aislada y snapshots de preservación del clon y del origen. No se hizo push,
merge, PR ni modificación de RutinaGo. No se generó APK ni se activó Etapa 3.
