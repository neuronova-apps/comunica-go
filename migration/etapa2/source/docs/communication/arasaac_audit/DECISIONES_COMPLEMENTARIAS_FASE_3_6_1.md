# Decisiones complementarias — Fase 3.6.1

**CONJUNTO REVALIDADO, NO APLICADO. Queda una resolución pendiente: Ruido (VOC_097).**

Este documento registra la instrucción posterior del usuario «FASE 3.6.1 — RESOLUCIÓN DE TRES AMBIGÜEDADES». Sustituye las conclusiones operativas anteriores únicamente para estos tres VOC. Los informes históricos, el JSON original y toda la evidencia quedan intactos. La instrucción posterior sobre Estoy sustituye expresamente el alcance anterior limitado a localización.

Rama `feature/comunicago-arasaac-visual-foundation`; HEAD/baseline `0f46c84fbe944ab83cabb0363b8641210255df8b`. Sin banco aplicado, commit, publicación, APK ni inicio de 3.6.2.

## Integridad y contrato del documento

JSON original: `comunicago-arasaac-decisiones-2026-10-19.json`; SHA-256 `75c12123adbee08486b140a297bca466f8c71c4f46aa3655c92b270929db1512`, idéntico antes/después. Banco local: `bfad0db80ceffcdfa0354381f0a097dd27316b5dcf7a4c85663108d4445d5be0`, idéntico antes/después y con contenido coincidente con baseline. El snapshot maestro y la copia binaria predecisiones permanecen intactos.

Matriz: SHA-256 recalculado `5c49bdc2614d6778ad1aae3f4f31044208a6bc8c252734f36c5a99d5bc1d8607`, conforme al contrato de serialización del constructor. Los 132 VOC, palabras y categorías coinciden con el banco y la matriz. Se comprobaron de nuevo las 122 selecciones originales: API/PNG 200, ID de respuesta, archivos capturados, hashes y decodificación PNG 500 × 500 (119 contenidos únicos). No se repitió ninguna consulta ARASAAC.

El JSON complementario usa un `kind` distinto y contiene resoluciones, referencias al original y un plan de revisión derivado con las 132 filas. No es un JSON sustituto para importar en la matriz original, ni modifica el original para fingir aplicación. `PENDIENTE` describe un bloqueo de revisión, no un nuevo estado del catálogo. El ID 7157 se conserva como referencia original retenida, no como una asociación neutral confirmada.

## Resoluciones y evidencia

| VOC / palabra | Selección original | Resolución de revisión | Motivo |
| --- | ---: | --- | --- |
| VOC_072 Estoy | 36392 | SOLO_TEXTO | 36392 representa localización. Es insuficiente para el significado general ahora indicado. Se retira solo de la propuesta aplicable, sin tocar el banco. [PNG](images/9b343aa711ec2a449a2217f9.png) · [API guardada](evidence/9c9428c6a0b64dae42bdd81d.json) |
| VOC_074 Dame | 17038 | SOLO_TEXTO | 17038 señala al emisor que entrega un objeto. Las definiciones oficiales son ofrecer, presentar/dar voluntariamente y poner en manos de otro. No demuestra inequívocamente la petición del hablante para recibir. Se cumple la decisión por defecto autorizada: SOLO_TEXTO. [PNG](images/5b5adb1ed0a50bea003b93a0.png) · [API guardada](evidence/26647a5542c016b373e869f9.json) |
| VOC_097 Ruido | 7157 | PENDIENTE | 7157 muestra una persona tapándose los oídos con expresión de malestar. La respuesta oficial incluye «me molesta el ruido» y la definición «Expresión utilizada cuando se quiere advertir a alguien de que el sonido ambiental le perturba». No confirma una representación neutral sin malestar. La autorización condicional para conservarlo no queda satisfecha. No se elige otro ID ni se autoriza SOLO_TEXTO por cuenta del agente. [PNG](images/1dffb2fb48936e7efa339548.png) · [API guardada](evidence/22375cd29c622151aa647e0f.json) |

### Estoy — VOC_072

SOLO_TEXTO provisional autorizado. Se mantienen los usos generales «Estoy aquí», «Estoy triste» y «Estoy cansado». No se restringe la palabra ni se busca otro ID. 36392 deja de formar parte del plan de asociaciones aplicables.

### Dame — VOC_074

SOLO_TEXTO provisional autorizado por la condición del usuario. La flecha del pictograma 17038 señala a quien entrega el objeto; las definiciones guardadas son dar/ofrecer/entregar. No demuestran inequívocamente la petición de recibir. No se renombra Dame ni se selecciona otro candidato.

### Ruido — VOC_097

La conservación de 7157 era condicional. La imagen expresa malestar por ruido y el significado oficial incluye «Expresión utilizada cuando se quiere advertir a alguien de que el sonido ambiental le perturba». No satisface el significado neutral solicitado. No se puede eliminar ese matiz llamándolo simplemente sonido percibido. No se cambia a 27073 ni a ningún otro ID; tampoco se decide SOLO_TEXTO sin autorización.

**Decisión necesaria:** autorizar provisionalmente SOLO_TEXTO para Ruido, o indicar una nueva decisión explícita que pueda contrastarse con la evidencia disponible. Hasta entonces queda una fila pendiente y el conjunto no está listo para confirmación final completa.

## Resultado de las 132 filas

| Resultado de revisión | Cantidad |
| --- | ---: |
| PRINCIPAL aplicable | 111 |
| ALTERNATIVO aplicable | 8 |
| Total de asociaciones pictográficas aplicables | 119 |
| SOLO_TEXTO autorizado | 12 |
| Pendiente: Ruido | 1 |
| Total VOC | 132 |

Las 129 decisiones ajenas a estos tres VOC conservan exactamente su decisión y su ID seleccionados. Ninguna de las 119 asociaciones aplicables usa un descarte ni un ID fuera del JSON original/matriz. Este resultado no aplica el banco y no constituye confirmación del conjunto final.

### Palabras SOLO_TEXTO autorizadas

VOC_057 El, VOC_058 La, VOC_059 Mi, VOC_060 Y, VOC_061 O, VOC_062 Con, VOC_072 Estoy, VOC_074 Dame, VOC_126 Me, VOC_127 Te, VOC_128 Está, VOC_129 Es.

### Plan individual revalidado

| VOC | Palabra | Categoría | Decisión original / ID | Resultado de revisión / ID | Aplicación al banco |
| --- | --- | --- | --- | --- | --- |
| VOC_001 | Yo | Personas y pronombres | PRINCIPAL / 6632 | PRINCIPAL / 6632 | NO APLICADO |
| VOC_002 | Tú | Personas y pronombres | PRINCIPAL / 6625 | PRINCIPAL / 6625 | NO APLICADO |
| VOC_003 | Mamá | Personas y pronombres | PRINCIPAL / 2458 | PRINCIPAL / 2458 | NO APLICADO |
| VOC_004 | Papá | Personas y pronombres | PRINCIPAL / 31146 | PRINCIPAL / 31146 | NO APLICADO |
| VOC_005 | Profesor | Personas y pronombres | PRINCIPAL / 6556 | PRINCIPAL / 6556 | NO APLICADO |
| VOC_006 | Quiero | Acciones | PRINCIPAL / 5441 | PRINCIPAL / 5441 | NO APLICADO |
| VOC_007 | Necesito | Acciones | PRINCIPAL / 37160 | PRINCIPAL / 37160 | NO APLICADO |
| VOC_008 | Comer | Acciones | PRINCIPAL / 6456 | PRINCIPAL / 6456 | NO APLICADO |
| VOC_009 | Beber | Acciones | PRINCIPAL / 6061 | PRINCIPAL / 6061 | NO APLICADO |
| VOC_010 | Ir | Acciones | PRINCIPAL / 8142 | PRINCIPAL / 8142 | NO APLICADO |
| VOC_011 | Jugar | Acciones | PRINCIPAL / 6537 | PRINCIPAL / 6537 | NO APLICADO |
| VOC_012 | Dormir | Acciones | PRINCIPAL / 6479 | PRINCIPAL / 6479 | NO APLICADO |
| VOC_013 | Descansar | Acciones | PRINCIPAL / 16643 | PRINCIPAL / 16643 | NO APLICADO |
| VOC_014 | Ayuda | Necesidades | PRINCIPAL / 12252 | PRINCIPAL / 12252 | NO APLICADO |
| VOC_015 | Agua | Comida y bebida | PRINCIPAL / 2248 | PRINCIPAL / 2248 | NO APLICADO |
| VOC_016 | Jugo | Comida y bebida | PRINCIPAL / 11461 | PRINCIPAL / 11461 | NO APLICADO |
| VOC_017 | Leche | Comida y bebida | PRINCIPAL / 2445 | PRINCIPAL / 2445 | NO APLICADO |
| VOC_018 | Pan | Comida y bebida | PRINCIPAL / 2494 | PRINCIPAL / 2494 | NO APLICADO |
| VOC_019 | Arroz | Comida y bebida | PRINCIPAL / 25556 | PRINCIPAL / 25556 | NO APLICADO |
| VOC_020 | Fruta | Comida y bebida | PRINCIPAL / 4653 | PRINCIPAL / 4653 | NO APLICADO |
| VOC_021 | Baño | Lugares | ALTERNATIVO / 6929 | ALTERNATIVO / 6929 | NO APLICADO |
| VOC_022 | Casa | Lugares | PRINCIPAL / 6964 | PRINCIPAL / 6964 | NO APLICADO |
| VOC_023 | Colegio | Lugares | PRINCIPAL / 3082 | PRINCIPAL / 3082 | NO APLICADO |
| VOC_024 | Aula | Lugares | PRINCIPAL / 9815 | PRINCIPAL / 9815 | NO APLICADO |
| VOC_025 | Parque | Lugares | PRINCIPAL / 2859 | PRINCIPAL / 2859 | NO APLICADO |
| VOC_026 | Feliz | Emociones y estados | PRINCIPAL / 32123 | PRINCIPAL / 32123 | NO APLICADO |
| VOC_027 | Triste | Emociones y estados | PRINCIPAL / 35545 | PRINCIPAL / 35545 | NO APLICADO |
| VOC_028 | Enojado | Emociones y estados | PRINCIPAL / 35539 | PRINCIPAL / 35539 | NO APLICADO |
| VOC_029 | Cansado | Emociones y estados | PRINCIPAL / 35537 | PRINCIPAL / 35537 | NO APLICADO |
| VOC_030 | Asustado | Emociones y estados | PRINCIPAL / 35535 | PRINCIPAL / 35535 | NO APLICADO |
| VOC_031 | Hola | Social | PRINCIPAL / 6522 | PRINCIPAL / 6522 | NO APLICADO |
| VOC_032 | Gracias | Social | PRINCIPAL / 8129 | PRINCIPAL / 8129 | NO APLICADO |
| VOC_033 | Por favor | Social | PRINCIPAL / 8195 | PRINCIPAL / 8195 | NO APLICADO |
| VOC_034 | Adiós | Social | PRINCIPAL / 6028 | PRINCIPAL / 6028 | NO APLICADO |
| VOC_035 | Qué | Preguntas | PRINCIPAL / 22620 | PRINCIPAL / 22620 | NO APLICADO |
| VOC_036 | Dónde | Preguntas | PRINCIPAL / 7764 | PRINCIPAL / 7764 | NO APLICADO |
| VOC_037 | Quién | Preguntas | PRINCIPAL / 9853 | PRINCIPAL / 9853 | NO APLICADO |
| VOC_038 | Cómo | Preguntas | PRINCIPAL / 22619 | PRINCIPAL / 22619 | NO APLICADO |
| VOC_039 | Grande | Descriptores | PRINCIPAL / 4658 | PRINCIPAL / 4658 | NO APLICADO |
| VOC_040 | Pequeño | Descriptores | PRINCIPAL / 4716 | PRINCIPAL / 4716 | NO APLICADO |
| VOC_041 | Caliente | Descriptores | PRINCIPAL / 26716 | PRINCIPAL / 26716 | NO APLICADO |
| VOC_042 | Frío | Descriptores | PRINCIPAL / 26865 | PRINCIPAL / 26865 | NO APLICADO |
| VOC_043 | Pelota | Objetos y juego | PRINCIPAL / 3241 | PRINCIPAL / 3241 | NO APLICADO |
| VOC_044 | Juguete | Objetos y juego | PRINCIPAL / 32568 | PRINCIPAL / 32568 | NO APLICADO |
| VOC_045 | Libro | Objetos y juego | PRINCIPAL / 2450 | PRINCIPAL / 2450 | NO APLICADO |
| VOC_046 | Cabeza | Cuerpo y salud | PRINCIPAL / 2673 | PRINCIPAL / 2673 | NO APLICADO |
| VOC_047 | Barriga | Cuerpo y salud | PRINCIPAL / 2786 | PRINCIPAL / 2786 | NO APLICADO |
| VOC_048 | Mano | Cuerpo y salud | PRINCIPAL / 2928 | PRINCIPAL / 2928 | NO APLICADO |
| VOC_049 | Dolor | Cuerpo y salud | PRINCIPAL / 30620 | PRINCIPAL / 30620 | NO APLICADO |
| VOC_050 | Ahora | Tiempo | PRINCIPAL / 32747 | PRINCIPAL / 32747 | NO APLICADO |
| VOC_051 | Después | Tiempo | PRINCIPAL / 32749 | PRINCIPAL / 32749 | NO APLICADO |
| VOC_052 | Primero | Tiempo | PRINCIPAL / 37753 | PRINCIPAL / 37753 | NO APLICADO |
| VOC_053 | No | Negación y regulación | PRINCIPAL / 5526 | PRINCIPAL / 5526 | NO APLICADO |
| VOC_054 | Parar | Negación y regulación | PRINCIPAL / 7196 | PRINCIPAL / 7196 | NO APLICADO |
| VOC_055 | Terminar | Negación y regulación | PRINCIPAL / 28429 | PRINCIPAL / 28429 | NO APLICADO |
| VOC_056 | Esperar | Negación y regulación | PRINCIPAL / 36914 | PRINCIPAL / 36914 | NO APLICADO |
| VOC_057 | El | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_058 | La | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_059 | Mi | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_060 | Y | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_061 | O | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_062 | Con | Conectores y determinantes | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_063 | Sí | Palabras núcleo | PRINCIPAL / 5584 | PRINCIPAL / 5584 | NO APLICADO |
| VOC_064 | Más | Palabras núcleo | PRINCIPAL / 5508 | PRINCIPAL / 5508 | NO APLICADO |
| VOC_065 | Otra vez | Palabras núcleo | PRINCIPAL / 37163 | PRINCIPAL / 37163 | NO APLICADO |
| VOC_066 | Aquí | Palabras núcleo | PRINCIPAL / 5382 | PRINCIPAL / 5382 | NO APLICADO |
| VOC_067 | Allí | Palabras núcleo | PRINCIPAL / 5375 | PRINCIPAL / 5375 | NO APLICADO |
| VOC_068 | Esto | Palabras núcleo | PRINCIPAL / 7095 | PRINCIPAL / 7095 | NO APLICADO |
| VOC_069 | Me gusta | Palabras núcleo | PRINCIPAL / 37721 | PRINCIPAL / 37721 | NO APLICADO |
| VOC_070 | No me gusta | Palabras núcleo | PRINCIPAL / 37825 | PRINCIPAL / 37825 | NO APLICADO |
| VOC_071 | Tengo | Acciones | PRINCIPAL / 32761 | PRINCIPAL / 32761 | NO APLICADO |
| VOC_072 | Estoy | Acciones | ALTERNATIVO / 36392 | SOLO_TEXTO / null | NO APLICADO |
| VOC_073 | Duele | Cuerpo y salud | PRINCIPAL / 30620 | PRINCIPAL / 30620 | NO APLICADO |
| VOC_074 | Dame | Acciones | ALTERNATIVO / 17038 | SOLO_TEXTO / null | NO APLICADO |
| VOC_075 | Abrir | Acciones | PRINCIPAL / 24825 | PRINCIPAL / 24825 | NO APLICADO |
| VOC_076 | Cerrar | Acciones | PRINCIPAL / 24976 | PRINCIPAL / 24976 | NO APLICADO |
| VOC_077 | Mirar | Acciones | PRINCIPAL / 6564 | PRINCIPAL / 6564 | NO APLICADO |
| VOC_078 | Escuchar | Acciones | PRINCIPAL / 6572 | PRINCIPAL / 6572 | NO APLICADO |
| VOC_079 | Sentarse | Acciones | PRINCIPAL / 6611 | PRINCIPAL / 6611 | NO APLICADO |
| VOC_080 | Levantarse | Acciones | ALTERNATIVO / 5099 | ALTERNATIVO / 5099 | NO APLICADO |
| VOC_081 | Lavarse | Higiene y autocuidado | ALTERNATIVO / 8977 | ALTERNATIVO / 8977 | NO APLICADO |
| VOC_082 | Bañarse | Higiene y autocuidado | PRINCIPAL / 6058 | PRINCIPAL / 6058 | NO APLICADO |
| VOC_083 | Cepillarse los dientes | Higiene y autocuidado | PRINCIPAL / 6971 | PRINCIPAL / 6971 | NO APLICADO |
| VOC_084 | Vestirse | Higiene y autocuidado | PRINCIPAL / 6627 | PRINCIPAL / 6627 | NO APLICADO |
| VOC_085 | Caminar | Acciones | PRINCIPAL / 6044 | PRINCIPAL / 6044 | NO APLICADO |
| VOC_086 | Salir | Acciones | PRINCIPAL / 6606 | PRINCIPAL / 6606 | NO APLICADO |
| VOC_087 | Entrar | Acciones | PRINCIPAL / 6491 | PRINCIPAL / 6491 | NO APLICADO |
| VOC_088 | Leer | Escuela y aprendizaje | PRINCIPAL / 7141 | PRINCIPAL / 7141 | NO APLICADO |
| VOC_089 | Escribir | Escuela y aprendizaje | PRINCIPAL / 2380 | PRINCIPAL / 2380 | NO APLICADO |
| VOC_090 | Pintar | Escuela y aprendizaje | PRINCIPAL / 5574 | PRINCIPAL / 5574 | NO APLICADO |
| VOC_091 | Hambre | Necesidades | PRINCIPAL / 35559 | PRINCIPAL / 35559 | NO APLICADO |
| VOC_092 | Sed | Necesidades | PRINCIPAL / 7273 | PRINCIPAL / 7273 | NO APLICADO |
| VOC_093 | Tranquilo | Emociones y estados | PRINCIPAL / 31310 | PRINCIPAL / 31310 | NO APLICADO |
| VOC_094 | Nervioso | Emociones y estados | PRINCIPAL / 30391 | PRINCIPAL / 30391 | NO APLICADO |
| VOC_095 | Aburrido | Emociones y estados | PRINCIPAL / 35531 | PRINCIPAL / 35531 | NO APLICADO |
| VOC_096 | Enfermo | Cuerpo y salud | PRINCIPAL / 7040 | PRINCIPAL / 7040 | NO APLICADO |
| VOC_097 | Ruido | Sensaciones y regulación sensorial | ALTERNATIVO / 7157 | PENDIENTE / null | NO APLICADO |
| VOC_098 | Silencio | Sensaciones y regulación sensorial | PRINCIPAL / 6071 | PRINCIPAL / 6071 | NO APLICADO |
| VOC_099 | Fuerte | Sensaciones y regulación sensorial | ALTERNATIVO / 38216 | ALTERNATIVO / 38216 | NO APLICADO |
| VOC_100 | Suave | Sensaciones y regulación sensorial | ALTERNATIVO / 38215 | ALTERNATIVO / 38215 | NO APLICADO |
| VOC_101 | Luz | Sensaciones y regulación sensorial | PRINCIPAL / 8619 | PRINCIPAL / 8619 | NO APLICADO |
| VOC_102 | Oscuro | Sensaciones y regulación sensorial | PRINCIPAL / 8643 | PRINCIPAL / 8643 | NO APLICADO |
| VOC_103 | Patio | Lugares | PRINCIPAL / 33064 | PRINCIPAL / 33064 | NO APLICADO |
| VOC_104 | Comedor | Lugares | PRINCIPAL / 9824 | PRINCIPAL / 9824 | NO APLICADO |
| VOC_105 | Dormitorio | Lugares | ALTERNATIVO / 15732 | ALTERNATIVO / 15732 | NO APLICADO |
| VOC_106 | Lápiz | Escuela y aprendizaje | PRINCIPAL / 2440 | PRINCIPAL / 2440 | NO APLICADO |
| VOC_107 | Cuaderno | Escuela y aprendizaje | PRINCIPAL / 2359 | PRINCIPAL / 2359 | NO APLICADO |
| VOC_108 | Mochila | Escuela y aprendizaje | PRINCIPAL / 2475 | PRINCIPAL / 2475 | NO APLICADO |
| VOC_109 | Tarea | Escuela y aprendizaje | PRINCIPAL / 11228 | PRINCIPAL / 11228 | NO APLICADO |
| VOC_110 | Galleta | Comida y bebida | PRINCIPAL / 8312 | PRINCIPAL / 8312 | NO APLICADO |
| VOC_111 | Manzana | Comida y bebida | PRINCIPAL / 2462 | PRINCIPAL / 2462 | NO APLICADO |
| VOC_112 | Plátano | Comida y bebida | PRINCIPAL / 2530 | PRINCIPAL / 2530 | NO APLICADO |
| VOC_113 | Sopa | Comida y bebida | PRINCIPAL / 2573 | PRINCIPAL / 2573 | NO APLICADO |
| VOC_114 | Ojos | Cuerpo y salud | PRINCIPAL / 2876 | PRINCIPAL / 2876 | NO APLICADO |
| VOC_115 | Boca | Cuerpo y salud | PRINCIPAL / 2663 | PRINCIPAL / 2663 | NO APLICADO |
| VOC_116 | Oídos | Cuerpo y salud | PRINCIPAL / 39498 | PRINCIPAL / 39498 | NO APLICADO |
| VOC_117 | Brazo | Cuerpo y salud | PRINCIPAL / 2669 | PRINCIPAL / 2669 | NO APLICADO |
| VOC_118 | Pierna | Cuerpo y salud | PRINCIPAL / 8666 | PRINCIPAL / 8666 | NO APLICADO |
| VOC_119 | Hermano | Personas y pronombres | PRINCIPAL / 2423 | PRINCIPAL / 2423 | NO APLICADO |
| VOC_120 | Hermana | Personas y pronombres | PRINCIPAL / 2422 | PRINCIPAL / 2422 | NO APLICADO |
| VOC_121 | Amigo | Personas y pronombres | PRINCIPAL / 8487 | PRINCIPAL / 8487 | NO APLICADO |
| VOC_122 | Amiga | Personas y pronombres | PRINCIPAL / 8486 | PRINCIPAL / 8486 | NO APLICADO |
| VOC_123 | Perdón | Social | PRINCIPAL / 11625 | PRINCIPAL / 11625 | NO APLICADO |
| VOC_124 | Buenos días | Social | PRINCIPAL / 6944 | PRINCIPAL / 6944 | NO APLICADO |
| VOC_125 | Buenas tardes | Social | PRINCIPAL / 6943 | PRINCIPAL / 6943 | NO APLICADO |
| VOC_126 | Me | Palabras núcleo | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_127 | Te | Palabras núcleo | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_128 | Está | Acciones | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_129 | Es | Acciones | SOLO_TEXTO / null | SOLO_TEXTO / null | NO APLICADO |
| VOC_130 | Quieres | Acciones | ALTERNATIVO / 5441 | ALTERNATIVO / 5441 | NO APLICADO |
| VOC_131 | Entiendo | Palabras núcleo | PRINCIPAL / 16659 | PRINCIPAL / 16659 | NO APLICADO |
| VOC_132 | Repite | Palabras núcleo | ALTERNATIVO / 37163 | ALTERNATIVO / 37163 | NO APLICADO |

## Archivos y comprobaciones

Comprobación de integridad de 1884 archivos anteriores de aplicación, auditoría, imágenes, scripts, informes, snapshot e IDE: sin cambios. `.idea/deploymentTargetSelector.xml` se conserva. Se mantienen el bloqueo visual y el fallback textual; no se tocaron perfiles, palabras, categorías, TTS, mensajes o RutinaGo.

Archivos nuevos: `docs/communication/resolve_arasaac_semantics_phase361.py`, este documento y `DECISIONES_COMPLEMENTARIAS_FASE_3_6_1.json`. No se actualizan ni sobrescriben informes anteriores. Las pruebas realizadas son de integridad, contrato y evidencia offline; no se ejecutaron compilaciones Android ni pruebas de dispositivo porque no hay cambios de aplicación.

**ESTOY_SOLO_TEXTO · DAME_SOLO_TEXTO · RUIDO_PENDIENTE · 132_VOC_REVALIDADOS · 129_DECISIONES_RESTANTES_INTACTAS · BANCO_SIN_APLICAR · SIN_COMMIT · SIN_APK**


## Resolución final posterior del usuario — 2026-10-19

Las conclusiones anteriores se conservan como historial. La instrucción posterior «FASE 3.6.1 — RESOLUCIÓN FINAL DE RUIDO» autoriza **SOLO_TEXTO provisional para VOC_097**, sin asignar 7157 ni otro ID. Estoy y Dame también quedan SOLO_TEXTO provisional. Se mantienen las tres palabras y sus significados, sin restricciones añadidas.

El conjunto final autorizado es **119 asociaciones pictográficas, 13 SOLO_TEXTO, 132 VOC y 18 categorías; cero decisiones pendientes o contradictorias**. El usuario autorizó aplicar ese conjunto, seguido de pruebas, APK y publicación exclusivamente en la rama de la fase si pasan las verificaciones obligatorias.

La curación local ya está aplicada y trazada en [RESOLUCION_FINAL_Y_APLICACION_FASE_3_6_1.md](RESOLUCION_FINAL_Y_APLICACION_FASE_3_6_1.md) y [APLICACION_CURACION_FASE_3_6_1.json](APLICACION_CURACION_FASE_3_6_1.json). El JSON original de revisión sigue intacto; el [JSON final derivado](DECISIONES_FINALES_FASE_3_6_1.json) contiene las tres resoluciones expresas. Los resultados técnicos definitivos y las limitaciones de validación manual se consultan en [VALIDACION_INTEGRACION_FASE_3_6_1.md](VALIDACION_INTEGRACION_FASE_3_6_1.md). No se actualiza Google Sheets ni se inicia 3.6.2.
