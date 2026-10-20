package com.neuronovaapps.comunicago.ui.screens.communication

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.neuronovaapps.comunicago.domain.communication.CommunicationProfileSetup
import com.neuronovaapps.comunicago.ui.components.ScreenColumn
import com.neuronovaapps.comunicago.ui.components.SectionHeading
import com.neuronovaapps.comunicago.ui.screens.arasaac.ARASAAC_ATTRIBUTION

@Composable
internal fun CommunicationSettingsScreen(
    profileName: String,
    setup: CommunicationProfileSetup?,
    onPersonalize: (() -> Unit)?,
    presentationControls: (@Composable () -> Unit)?,
    onBackToModules: () -> Unit,
    speechSettings: (@Composable () -> Unit)? = null,
    accessibilityContent: (@Composable () -> Unit)? = null,
) {
    ScreenColumn(Modifier.safeDrawingPadding().testTag("communication-settings")) {
        SectionHeading("Configuración de ComunicaGo")
        Text("Perfil activo: $profileName")
        speechSettings?.invoke()
        SectionHeading("Personalización del perfil")
        if (setup != null) {
            Text("Rango de edad: ${setup.ageRange.setupLabel}")
            Text("Comunicación oral: ${setup.oralCommunication.setupLabel}")
        }
        if (onPersonalize != null) OutlinedButton(onClick = onPersonalize,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("communication-personalize")) {
            Text("Editar personalización")
        }
        presentationControls?.invoke()
        SectionHeading("Accesibilidad")
        accessibilityContent?.invoke()
        SectionHeading("Pictogramas y licencia")
        Text(ARASAAC_ATTRIBUTION, Modifier.testTag("communication-arasaac-attribution"))
        Text("Los pictogramas se obtienen de ARASAAC por su identificador validado y se guardan en este dispositivo. Sin conexión se muestran las imágenes guardadas; si faltan, puedes seguir usando las palabras. No se envían nombres, datos de perfil ni mensajes. ComunicaGo mantiene su uso gratuito y sin fines comerciales.")
        OutlinedButton(onClick = onBackToModules,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("communication-back-to-modules")) {
            Text("Gestionar perfiles")
        }
    }
}
