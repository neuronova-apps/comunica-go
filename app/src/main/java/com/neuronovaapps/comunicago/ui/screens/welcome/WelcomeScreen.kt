package com.neuronovaapps.comunicago.ui.screens.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neuronovaapps.comunicago.ui.theme.LocalHighContrast

/** NeuroNova welcome pattern, with a fixed action outside the scrollable introduction. */
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Surface(Modifier.fillMaxSize().testTag("comunicago-welcome"), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val compact = maxHeight < 440.dp
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 24.dp, Alignment.CenterVertically),
                ) {
                    CommunicationMark(compact)
                    Text("ComunicaGo", style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black, textAlign = TextAlign.Center,
                        color = if (LocalHighContrast.current) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { heading() })
                    Text("Herramienta de apoyo a la Comunicación Aumentativa y Alternativa mediante palabras, pictogramas y voz.",
                        style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Palabras", "Pictogramas", "Voz").forEach { label ->
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                                Text(label, Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onStart,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("welcome-start")) {
                Text("Comenzar", textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))
            Text("NeuroNova Apps", style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CommunicationMark(compact: Boolean) {
    // Local geometric identity following the provisional NeuroNova mark; no external images.
    Surface(Modifier.size(if (compact) 96.dp else 136.dp).clearAndSetSemantics { },
        shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)) {
        Box(contentAlignment = Alignment.Center) {
            Surface(Modifier.size(if (compact) 64.dp else 82.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("CG", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
