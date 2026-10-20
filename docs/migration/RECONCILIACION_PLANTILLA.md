# Reconciliación de la plantilla Android

Fecha: 20 de octubre de 2026 (America/Lima).

Proyecto definitivo: `G:/NeuroNovaApps/AndroidStudioProjects/ComunicaGo`.
Repositorio: `https://github.com/neuronova-apps/comunica-go.git`.
Rama: `feature/comunicago-standalone-foundation`.
Antecesor conservado: `95f78ed44cb82a9813cf8f6018e43754e67cee79`.

## Respaldo y hallazgo

Antes de modificar se copiaron y verificaron por SHA-256 los 71 archivos de la
plantilla, incluidos archivos ocultos, configuración local y cachés existentes.
Respaldo externo:
`C:/Users/PC/.codex/visualizations/2026/10/20/01a15b51-9dcc-71c0-9d97-1afadad979b3/ComunicaGo-reconciliacion-20261020/`.
`plantilla-manifest-sha256.json` permite verificar la copia `plantilla-original/`.

La raíz no pertenecía a un repositorio Git. Se encontró un repositorio anidado en
`app/src/main/java/com/neuronovaapps/comunicago/.git`: rama master sin commits,
sin remotes, MainActivity.kt y ui/ sin versionar. Por tanto, la afirmación de la
auditoría anterior «sin .git» solo era correcta para la raíz.
La carpeta anidada completa se conservó como `git-anidado-original/` en el respaldo;
también está incluida en la copia original. No se eliminaron archivos Kotlin.

## Integración aplicada

Se inspeccionó el clon ComunicaGo-Git, limpio y con solo README.md. Se generó y
verificó `comunica-go-original.bundle` con todas sus referencias. Se inicializó
Git en la raíz definitiva y se recuperaron desde ese bundle el commit original y
las referencias main y feature. Se estableció origin y el upstream de la rama.
El índice se inició desde el árbol original sin hacer checkout sobre la plantilla.
Solo se restauró README.md, cuya ruta no existía en la plantilla.

GitHub anunció main y feature en el mismo commit 95f78ed durante la verificación.
El nuevo historial continúa ese commit; no incorpora el historial de RutinaGo.

Los archivos Android, MainActivity, temas, iconos, Gradle y wrapper permanecen
idénticos al respaldo. La única adaptación de un archivo existente es
`.idea/vcs.xml`: el mapping Git pasa de la carpeta Kotlin a `$PROJECT_DIR$`.
Su versión anterior está respaldada. No se versionan local.properties, cachés,
workspace.xml, APK ni secretos.

ComunicaGo-Git queda intacto y se conserva como referencia. RutinaGo se utiliza
solo en lectura para verificar su preservación; la extracción utiliza una copia
aislada del bundle validado. No se hicieron push, merge, PR ni cambios en main.

## Alcance siguiente

La Etapa 2 extrae la fuente validada y enumera las adaptaciones pendientes.
La Etapa 3 ensambla el runtime independiente. La plantilla sigue siendo el punto
de entrada Android hasta esa adaptación; esta reconciliación no acredita una APK
funcional de ComunicaGo ni ejecuta las etapas posteriores.
