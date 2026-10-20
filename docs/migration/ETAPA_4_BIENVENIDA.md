# Etapa 4 — bienvenida de ComunicaGo

Fecha: 20 de octubre de 2026, America/Lima.

Rama conservada: `feature/comunicago-standalone-runtime`.
HEAD inicial: `47980795e6095badaf5fce3c14c0e1d55aafa6a6`. Árbol inicialmente limpio.
El usuario informó validación satisfactoria de la APK de Etapa 3 en celular;
esta etapa no declara una nueva validación real de la APK modificada.

## Respaldo y referencia

Bundle nuevo verificado antes de editar:
`G:/NeuroNovaApps/Backups/ComunicaGo-PreBienvenida-Etapa4-e5cd5c77696a4719a9cee2f1fc1b8bc8.bundle`.
SHA-256: `6f77718970bbc5647370b992035074d804f47d65937ba186250f84dee9e56736`.
Contiene el commit inicial con historial completo, sin prerrequisitos externos.
No se sustituyó ningún respaldo anterior.

Se leyó únicamente como referencia visual `WelcomeScreen.kt` de RutinaGo:
marca geométrica provisional local, título destacado, formas redondeadas,
colores del tema, texto centrado, etiquetas descriptivas, márgenes de 24 dp,
acción inferior y marca NeuroNova Apps. No se trasladan el símbolo de tareas,
textos de rutinas ni componentes funcionales de RutinaGo.

## Archivos de implementación y pruebas

- `app/src/main/java/com/neuronovaapps/comunicago/ui/screens/welcome/WelcomeScreen.kt`:
  nueva bienvenida propia, con nombre ComunicaGo, descripción CAA solicitada,
  marca local CG, etiquetas Palabras/Pictogramas/Voz y NeuroNova Apps.
- `app/src/main/java/com/neuronovaapps/comunicago/ComunicaGoApp.kt`: incorpora
  exclusivamente un estado de entrada y la llamada a la bienvenida antes del
  flujo existente. No modifica RuntimeContent ni sus reglas.
- `app/src/androidTest/java/com/neuronovaapps/comunicago/WelcomeNavigationTest.kt`:
  tres pruebas de aparición/entrada, preferencias intactas, recreación y Atrás.
- `app/src/androidTest/java/com/neuronovaapps/comunicago/WelcomeAccessibilityTest.kt`:
  prueba de 45 escenarios, con los temas Día/Noche/Sistema y alto contraste,
  las tres escalas y tamaños 400×760, 320×480 y 480×320 dp, más escala Android 1.6.
- `app/src/androidTest/java/com/neuronovaapps/comunicago/StandaloneRuntimeTestBase.kt`:
  el helper de entrada pulsa Comenzar cuando la bienvenida está presente, para
  conservar las pruebas de regresión existentes sin reescribir sus escenarios.
- Este informe y `VALIDACION_ETAPA_4_BIENVENIDA.json`: resultados y hashes.

## Composición, accesibilidad y navegación

Comenzar permanece fuera del contenido desplazable, en el bloque inferior,
con ancho disponible y altura mínima 56 dp. Márgenes horizontales 24 dp y
verticales 20 dp; safeDrawingPadding respeta barras del sistema y recortes.
NeuroNova Apps aparece debajo del botón, separado por 20 dp. En alturas reducidas
la marca disminuye y el contenido introductorio puede desplazarse; el botón
permanece fijo, sin superponerlo al contenido ni exigir desplazarlo para entrar.

Se reutilizan tema, contraste y escalas existentes sin nuevas preferencias.
El título tiene semántica de encabezado; el botón tiene etiqueta textual;
la marca geométrica es decorativa para evitar lectura duplicada. El orden es
introducción, acción y marca. No hay imágenes externas ni servicios nuevos.
La comprobación efectiva con TalkBack y las medidas en dispositivo quedan
pendientes: los escenarios de accesibilidad se compilaron, no se ejecutaron.

Cada arranque nuevo muestra la bienvenida. Comenzar cambia un booleano temporal
rememberSaveable: abre directamente el flujo actual, sin selector de módulos.
Si existe un perfil configurado llega a Inicio; en una instalación sin perfil
continúa la creación explícita existente, sin crear perfiles automáticamente.
La recreación conserva la entrada ya realizada y el estado actual.
No se añade una pila de navegación ni se cambia Atrás dentro del comunicador:
continúa abriendo gestión de perfiles y regresando conforme al flujo validado.
Atrás en la bienvenida conserva el comportamiento normal de Android.

## Verificación y APK

Se ejecutó offline con JAVA_HOME del JBR instalado:

```powershell
.\gradlew.bat --offline --no-configuration-cache --max-workers=2 testDebugUnitTest assembleDebug lintDebug assembleDebugAndroidTest
.\tools\verify_stage3.ps1
```

- testDebugUnitTest: 170 ejecutadas, 170 aprobadas, cero fallos/errores.
- assembleDebug: aprobado; APK actualizada generada.
- lintDebug: aprobado, cero errores, 10 advertencias anteriores: etiqueta
  redundante, recursos de plantilla sin uso, sugerencia de versión Coil y
  restricción de versión inline. No aparecen nuevas advertencias de bienvenida.
- assembleDebugAndroidTest: aprobado. Nuevas pruebas instrumentadas compiladas;
  no ejecutadas ni declaradas aprobadas en dispositivo.
- adb devices -l: lista vacía. No se inició un emulador ni se instaló la APK.
- Banco: hash y 132 pares curatoriales comprobados; 132 vocablos, 18 categorías,
  119 asociaciones y 13 SOLO_TEXTO. Los 102 archivos de Etapa 2 siguen intactos.
- Sin cambios en fuentes de perfiles/DataStore, WorkManager, caché, TTS,
  velocidad, dominio, vocabulario, navegación inferior, Gradle ni Manifest.

Copia identificada:
`G:/NeuroNovaApps/AndroidStudioProjects/ComunicaGo/app/build/outputs/apk/etapa4/ComunicaGo-Bienvenida-Etapa4.apk`.
SHA-256: `970706a979fe262d915c5147841a9a3412e315c5bd0da6708c07c7ac90c01f90`.
Tamaño: 15.307.207 bytes. Copia contrastada con el APK debug generado.
Log local: `build/etapa4-gradle.log`.

## Preservación y cierre

RutinaGo se usó exclusivamente en lectura. Sin modificaciones de sus archivos,
base, perfiles, APK, ramas o commits. ComunicaGo-Git no fue modificado.
El repositorio queda en la misma rama con commit local selectivo de bienvenida.
No se hizo push, merge ni PR. No se iniciaron fases pictográficas ni otra etapa.
Pendiente: ejecutar las pruebas instrumentadas y verificar visualmente esta
bienvenida en un dispositivo disponible, incluyendo TalkBack y texto grande.
