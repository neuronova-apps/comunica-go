# Etapa 3 — runtime Android independiente

Fecha: 20 de octubre de 2026, America/Lima.

Integración local en `feature/comunicago-standalone-runtime`, creada desde
`20184b8368c47c99fc4b5c856bfd0b1941bb2cea`, con árbol inicialmente limpio.
Sin fetch, push, merge ni PR durante esta etapa. No se inicia Etapa 4 o 5.

## Respaldo previo

Bundle nuevo, sin sustituir respaldos:
`G:/NeuroNovaApps/Backups/ComunicaGo-Etapa3-20261020-133307ae3ebf48ffb3683eadc2d2bb68.bundle`.

SHA-256: `482e2578fa717ff5d7c849ca47454233f68a01fccc274ae949c2f6ddb632a0c6`.
`git bundle verify` aprobó el bundle y confirmó historial completo, sin
prerrequisitos externos. `git bundle list-heads` confirmó la referencia
foundation en el commit esperado. Se verificó antes de crear la rama runtime.

## Integración y límites

- MainActivity abre ComunicaGoApp, sin Greeting ni selector de módulos.
  Se preservan applicationId/namespace `com.neuronovaapps.comunicago`, minSdk 24,
  compileSdk/targetSdk 37 y la configuración original de Android Studio.
- Dominio, catálogo, constructor, preferencias contextuales, pictogramas,
  adaptador TTS y pantallas de comunicación provienen del staging auditado.
  Se adaptan paquetes/imports; `migration/etapa2` permanece fuera de source sets.
- Inicio, Mi vocabulario y Configuración conservan búsqueda local secundaria,
  mensaje compartido, repeticiones, quitar/limpiar/deshacer, organización de
  Inicio y ocultación/recuperación de palabras. La salida del antiguo host abre
  gestión de perfiles. El estado temporal se separa por ID estable y se conserva
  mediante SaveableStateHolder; no se guarda historial de mensajes en DataStore.
- Se seleccionaron símbolos de los archivos mixtos: registro/apariencia sin
  AppMode, componentes Compose sin RoutineCard, pantallas de perfiles adaptadas,
  atribución sin buscador y cola WorkManager sin scheduler de rutinas.
  No se copiaron los 25 archivos de referencia completos al runtime.
- Tema Día/Noche/Sistema, tres escalas de texto, alto contraste, estados,
  semántica y controles de accesibilidad propios en Configuración. Se conserva
  el tema funcional aprobado; los iconos y recursos originales de plantilla
  permanecen en el proyecto.

## Perfiles y almacenamiento

DataStore `comunicago_preferences`: UUID/nombre, selección y apariencia propia.
Crear, renombrar y seleccionar conservan ID; IDs duplicados se rechazan.
La UI permite eliminar el perfil activo con confirmación explícita.

`CommunicationSetupStorage`, singleton de proceso con applicationContext propio,
mantiene `communication_profile_setup.preferences_pb` en noBackupFilesDir.
Se conservan cinco familias por perfil: contexto, presentación, disponibilidad,
asignación a Inicio y velocidad. Null sigue significando no configurado;
conjunto vacío significa selección vacía explícita. Ocultar no elimina una
asignación a Inicio; recuperar vuelve a mostrarla si sigue asignada.

El manager conserva el mutex compartido entre instancias y elimina las cinco
familias antes de borrar el registro. Son dos archivos DataStore, sin prometer
una transacción global entre ambos. No hay importación automática de datos.

## ARASAAC

Catálogo exacto SHA-256:
`666d24b453f29a360d5bd42547a5c57ff2256b4430622714a05bfbdb21c3d718`.
132 vocablos, 18 categorías, 119 asociaciones aprobadas (116 IDs distintos),
13 SOLO_TEXTO. Los 132 pares se contrastan con la matriz auditada.

Solo se descargan PNG de IDs aprobados observados por la UI. El repositorio
prioriza caché privada, publica imágenes locales con Coil y mantiene fallback
textual. Se conservan límites PNG, descarga controlada, concurrencia tres,
trabajo único KEEP, restricción de red y backoff. El worker consulta únicamente
la allowlist del catálogo; no abre bases ni catálogos de RutinaGo.
No se hacen búsquedas HTTP de vocabulario ni nuevas consultas masivas.
No se inicia Fase 3.6.2. Créditos y licencia ARASAAC visibles en Configuración.

DataStore, WorkManager, Coroutines, Gson, OkHttp y Coil usan versiones de la
referencia auditada. WorkManager depende internamente de Room: se aplica una
restricción a room-runtime 2.8.5, disponible en caché, porque faltaba 2.7.0
offline. Esto no incorpora Room/KSP ni modelos, DAOs o bases de RutinaGo al código.
No se añade coil-network-okhttp: Compose consume archivos locales.
Manifest incorpora INTERNET, estado de red y visibilidad del servicio TTS.
Backup y transferencia excluyen PNG reconstruibles; contexto queda en noBackup.

## Voz

Se conserva motor Android español y controlador aprobado, con Hablar, Repetir,
Detener, velocidad por perfil, callbacks/generaciones y watchdogs. ON_STOP detiene
voz; al disponer la sesión se libera el motor. No hay servicios externos añadidos
ni audio persistido. La audibilidad depende del motor instalado y queda pendiente
de verificación real en dispositivo.

## Validación

Comando offline (JAVA_HOME del JBR instalado, sin cambiar archivos del SDK):

```powershell
.\gradlew.bat --offline --no-configuration-cache --max-workers=2 testDebugUnitTest assembleDebug lintDebug assembleDebugAndroidTest
.\tools\verify_stage3.ps1
```

Los resultados exactos y hashes de APK están en `VALIDACION_ETAPA_3.json`.
Los escenarios antiguos que esperaban selector/navegación de RutinaGo se
sustituyen por escenarios de runtime independiente; las suites de componentes,
curación, accesibilidad y voz se adaptan al paquete nuevo. Pruebas offline omiten
únicamente scheduler de rutinas y resolver visual de rutinas. Se añaden las
pruebas de apariencia y worker seleccionadas de los archivos de referencia.

Las primeras ejecuciones detectaron entradas duplicadas del catálogo TOML y
rutas de fixtures del origen; se corrigieron antes de la verificación final.
Gradle necesitó acceso a sus cachés fuera del sandbox. ADB funciona con ese
permiso y `adb devices -l` devolvió una lista vacía: las pruebas instrumentadas
se compilan, pero no se ejecutan ni se declaran aprobadas en dispositivo.
No hay validación final en celular.

Los warnings de lint se conservan registrados: etiqueta redundante de actividad,
recursos originales sin uso, sugerencia de versión Coil y versión inline de la
restricción transitiva. No se cambian versiones aprobadas solo por esa sugerencia.

## Preservación y Etapa 4 pendiente

No se abrió ni modificó el proyecto RutinaGo o ComunicaGo-Git: sin operaciones
sobre sus archivos, perfiles, bases, ramas, commits o APK. El trabajo de código
se limita a ComunicaGo; el único respaldo nuevo está en la carpeta autorizada.
Las 102 fuentes/evidencias de Etapa 2 conservan sus SHA-256.

Pendiente, solo con autorización para Etapa 4: ejecutar suites instrumentadas
en emulador/dispositivo, comprobar voz audible y ciclo de vida, TalkBack, texto
grande y rotación; verificar descargas/caché offline con red real y revisar la
experiencia completa de perfiles. Publicación GitHub queda pendiente para una
solicitud posterior; no se investigó conectividad en esta etapa.

Estado: COMUNICAGO_RUNTIME_INDEPENDIENTE integrado y compilado;
RUTINAGO_INTACTO; BANCO_ARASAAC_PRESERVADO; PERFILES_INDEPENDIENTES;
NAVEGACION_FUNCIONAL integrada; TTS_INTEGRADO; RESPALDO_LOCAL_VERIFICADO.
Estos estados de integración no sustituyen la validación pendiente en celular.
