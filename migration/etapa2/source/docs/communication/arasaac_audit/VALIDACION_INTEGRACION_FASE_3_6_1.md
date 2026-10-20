# Integración curada de ARASAAC — Fase 3.6.1

Rama exclusiva: `feature/comunicago-arasaac-visual-foundation`. Baseline: `0f46c84fbe944ab83cabb0363b8641210255df8b`. La entrega termina en una APK para validación manual; no incluye 3.6.2–3.6.5 ni merge o PR.

## Decisiones y banco

La resolución final del usuario autoriza **119 asociaciones pictográficas y 13 vocablos SOLO_TEXTO**, manteniendo **132 VOC y 18 categorías**. Estoy (VOC_072), Dame (VOC_074) y Ruido (VOC_097) conservan su palabra y pasan provisionalmente a texto; no se aplican 36392, 17038 ni 7157 a esos VOC. Las otras 129 decisiones mantienen su selección original. Los tres pares de palabras que comparten imagen conservan sus etiquetas y funciones propias: hay 119 asociaciones y 116 IDs distintos.

El JSON original permanece byte por byte: SHA-256 `75c12123adbee08486b140a297bca466f8c71c4f46aa3655c92b270929db1512`. Las resoluciones posteriores están documentadas en [la resolución final](RESOLUCION_FINAL_Y_APLICACION_FASE_3_6_1.md) y el [JSON final derivado](DECISIONES_FINALES_FASE_3_6_1.json), SHA-256 `359c68cb33c188b45c41e85a5a97bb4f86b1ae362ef1c65a7a6e2b6eb3ca3355`.

El banco anterior se conserva en `communication_catalog_v1.original_pre_decisiones.json`, SHA-256 `bfad0db80ceffcdfa0354381f0a097dd27316b5dcf7a4c85663108d4445d5be0`. El catálogo curado tiene SHA-256 `666d24b453f29a360d5bd42547a5c57ff2256b4430622714a05bfbdb21c3d718`. El [informe estructurado de aplicación](APLICACION_CURACION_FASE_3_6_1.json) enumera ID anterior, ID autorizado, estado, estrategia y campos modificados por cada VOC.

Estados según el contrato real: 119 `VALIDADO` + `PICTOGRAMA_VALIDADO`, con ID positivo; 13 `PENDIENTE_ARASAAC` + `SOLO_TEXTO`, con ID vacío en el catálogo y nulo en las decisiones. Ese estado editorial indica ausencia de imagen; **no significa que queden decisiones del usuario pendientes**. No se cambiaron etiquetas, VOC, categorías, orden, indicadores de modos ni metadatos gramaticales. Los cambios de curación están limitados a ID, estado/estrategia y sus metadatos de justificación. La cabecera identifica la curación local y sus huellas, sin atribuirla a una actualización de Sheets.

El snapshot maestro `docs/communication/source_v1.json` sigue con SHA-256 `4f49c92c6d1c569c8d50e9e0108ce5de1c1e7560b5105a3a91750286e5ed9df4`. Google Sheets permanece sin cambios. Los informes de auditoría y matrices previos son evidencia histórica de la etapa anterior a la aprobación; no deben interpretarse como el estado del catálogo curado.

## Integración compartida

Se reutilizan `ArasaacImageUrls`, `PictogramDownloader`, `PictogramFileStore`, `PictogramDownloadTask`, `PictogramDownloadWorker` y `WorkManagerPictogramQueue`. El cliente de búsqueda/detalle `ArasaacHttpRepository` no cambia y no se busca por palabra para sustituir IDs. ComunicaGo aporta un adaptador de observación y autorización del catálogo, no otro cliente HTTP ni otro almacén.

La caché sigue en `files/pictograms/arasaac/<ID>.png`, compartida con RutinaGo. El worker acepta además los IDs aprobados del catálogo; la consulta previa de referencias personales de RutinaGo y su reconciliación permanecen intactas. Los trabajos mantienen el nombre único `arasaac-pictogram-<ID>` y política KEEP. Un semáforo común limita a tres las descargas simultáneas entre ambos módulos. Leer el catálogo no programa descargas: se solicitan al observar palabras visibles y solo cuando falta una imagen válida.

Con caché válida se muestra la imagen sin consultar la URL. Sin caché, conectividad o ante errores, la palabra sigue seleccionable. HTTP 404 y PNG inválido se rechazan; los errores transitorios siguen la política de reintento existente. No se reutilizan asociaciones antiguas por palabra ni se borran imágenes personales: el nuevo VOC resuelve únicamente su ID aprobado. Las tres palabras textuales salen antes de crear el repositorio o consultar caché.

Inicio, Buscar más palabras y las vistas previas de vocabulario mantienen palabra y selección. La tarjeta completa conserva una acción de comunicación. Los espacios de imagen son estables durante la carga; se usa `ContentScale.Fit`, sin deformar ni recortar. El modo guiado reserva 144 dp y el completo 112 dp en las tarjetas principales. Las imágenes son decorativas para accesibilidad: no anuncian una segunda etiqueta ni IDs. Se conserva atribución ARASAAC en Configuración.

TTS, texto/orden/repeticiones del mensaje, perfiles, DataStore/Room, navegación y funcionalidades de RutinaGo no se modifican. Los únicos cambios compartidos son la autorización adicional del worker y el límite de concurrencia. Los cambios de otros archivos ajenos al catálogo/pictogramas son ajustes de pruebas para verificar el comportamiento vigente.

## Verificaciones y evidencia

Las cuatro tareas `testDebugUnitTest`, `assembleDebug`, `lintDebug` y `assembleDebugAndroidTest` aprobaron. Hay **205 pruebas unitarias, cero fallos/errores**, incluyendo la comparación de cada par VOC/ID con las decisiones finales y la conservación de identidad/campos no curados. Lint: **cero errores, seis advertencias y una sugerencia heredadas**.

La prueba auténtica realizó GET de `https://static.arasaac.org/pictograms/2248/2248_500.png` mediante el descargador existente en el emulador, decodificó PNG 500 × 500 y obtuvo SHA-256 `88df8a0a982fe481b80fd97dd3ad2e1f6c70ce1df19bf401d0327daac9731d81`, idéntico al auditado. Verificó el worker y la reutilización del archivo compartido al reconstruir el almacén. [Registro de descarga real](integration_evidence/authentic-download.txt).

Las pruebas de componente con flujos controlados se distinguen de esa descarga: una recorre las 132 tarjetas sin internet, verifica las 119 observaciones permitidas y el texto/orden del mensaje; otra renderiza el PNG auténtico guardado en 24 combinaciones de guiada/completa, Día/Noche/Sistema/alto contraste y los tres tamaños reales de texto, y comprueba el fallback al fallar y la altura estable. [Guiada / Día / texto Muy grande](integration_evidence/GUIDED-0-large.png), [Completa / alto contraste / texto Muy grande](integration_evidence/FULL-3-large.png).

Resultado definitivo: **INTEGRATION_GATES_PASSED**, registrado en [VALIDACION_INTEGRACION_FASE_3_6_1.json](VALIDACION_INTEGRACION_FASE_3_6_1.json). Pasaron **35 métodos instrumentados distintos**: tres de pictogramas/fallback y 32 de regresión/accesibilidad. Los seis casos afectados de perfiles/vocabulario/presentación aprobaron en 83,980 s; las tres matrices ampliadas aprobaron en 368,496 s. El verificador confirma que cada método esperado tiene resultado posterior aprobado, incluidos todos los casos que fallaron en intentos previos. No quedan fallos instrumentados sin resolver.

Se conservan los intentos fallidos y correcciones en `app/build/reports/phase361-curated/`: error de compilación de una aserción nueva, retorno no-void de un test JUnit nuevo y de dos limpiezas antiguas, búsquedas de nodos aún no materializados en listas perezosas, teclado abierto y política Atrás antigua en pruebas. Los arneses con flujos asíncronos utilizan un reloj UI controlado para evitar aplicar recomposiciones/disponer SlotTable desde un hilo ajeno. Se conservaron las pruebas y sus comprobaciones funcionales; los recuentos y recorridos se adaptaron al catálogo autorizado y a la navegación vigente.

Las matrices históricas crean escenarios de 400 × 760 y 320 × 480 dp; se ejecutan con un viewport físico capaz de contener ambos, para evitar recortar el propio banco de pruebas. La aplicación y las pruebas nuevas/regresiones principales se verifican también en **320 × 480 dp físicos**. El emulador vuelve a 720 × 1080 px a densidad 360 (320 × 480 dp) al terminar. Las matrices incluyen simulación de escala de fuente del sistema y Sistema claro/oscuro, además de los tamaños de texto de la aplicación.

## APK y límites de la validación

La APK verificada es `app/build/outputs/apk/debug/app-debug.apk`, SHA-256 `9f0ec10ac53516057719afa3c10acb37214220d29ca920aa49416822f8d6c4f2`. El empaquetado autorizado crea `RutinaGo-ComunicaGo-ARASAAC-fase-3-6-1.apk` con idénticos bytes solo después de superar todos los controles.

Se conservan applicationId `com.neuronovaapps.rutinago` y firma debug SHA-256 `ff36e3c58c80a5d345625ee32a1c02910e27aed95e3503256733475c61131846`, comparada con la APK TTS anterior. Las instalaciones de pruebas son actualizaciones `adb install -r` únicamente en el emulador, sin desinstalar ni borrar datos. La APK anterior de TTS se preservó con SHA-256 `298384c124d903f783138f56bbdc1c8f4adf499a4a3620584c3743b6ec40937d` y se conserva como respaldo.

La validación final en celular y la audición física de TTS/TalkBack quedan a cargo de la revisión manual solicitada. Las pruebas automáticas comprueban semántica accesible y regresiones, no una aprobación clínica ni que las 119 imágenes se hayan inspeccionado individualmente en el dispositivo. Los 119 pares sí se contrastaron con la evidencia visual/semántica ya guardada. No se inicia ninguna fase posterior.

## Contenido revisado para publicación

El inventario completo de rutas concretas está en [ARCHIVOS_PUBLICACION_FASE_3_6_1.txt](ARCHIVOS_PUBLICACION_FASE_3_6_1.txt). Incluye la integración, pruebas, decisiones, respaldo original, scripts e informes y evidencia pública necesaria para reproducir la auditoría/matriz offline. Los 251 PNG de auditoría incluidos son exactamente los referenciados por las fichas de revisión; se añaden un fixture de prueba y ocho capturas de validación (260 PNG en total). No se incorporan las láminas JPG regenerables, un PNG de diagnóstico sin referencia, descargas temporales, bases de perfiles, APK ni `.idea/deploymentTargetSelector.xml`.

Las huellas del original de decisiones, respaldo del catálogo y JSON final se comprobaron también contra los bytes del índice Git. Se preservan sus finales de línea originales mediante atributos específicos de la carpeta de evidencia; se permite CRLF en su comprobación de espacios sin reescribir los archivos originales. El catálogo empaquetado y el snapshot maestro conservan LF estable. No se utiliza `git add -A`.
