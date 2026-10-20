package com.neuronovaapps.comunicago

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.neuronovaapps.comunicago.data.preferences.*
import com.neuronovaapps.comunicago.ui.components.ScreenColumn
import com.neuronovaapps.comunicago.ui.components.SectionHeading
import com.neuronovaapps.comunicago.ui.screens.communication.CommunicationProfileEntryScreen
import com.neuronovaapps.comunicago.ui.screens.communication.components.CommunicationChoiceOption
import com.neuronovaapps.comunicago.ui.screens.profiles.FirstProfileScreen
import com.neuronovaapps.comunicago.ui.screens.profiles.ProfilesScreen
import com.neuronovaapps.comunicago.ui.theme.ComunicaGoTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun ComunicaGoApp() {
    val context = LocalContext.current.applicationContext
    val repository = remember(context) { UserPreferencesRepository(context) }
    var readError by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    val preferences by produceState<UserPreferences?>(null, repository, retry) {
        readError = false
        try { repository.preferences.collect { value = it } }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { readError = true }
    }
    val current = preferences
    ComunicaGoTheme(
        darkTheme = (current?.themeMode ?: ThemeMode.DAY).usesDarkTheme(isSystemInDarkTheme()),
        textScale = current?.textScaleMode?.scaleFactor ?: 1f,
        highContrast = current?.highContrast ?: false,
    ) {
        Surface(Modifier.fillMaxSize()) {
            if (current == null) {
                ScreenColumn(Modifier.safeDrawingPadding()) {
                    Text(if (readError) "No se pudieron cargar los perfiles." else "Cargando ComunicaGo…")
                    if (readError) Button(onClick = { retry++ }) { Text("Reintentar") }
                }
            } else {
                RuntimeContent(current, repository)
            }
        }
    }
}

@Composable
private fun RuntimeContent(preferences: UserPreferences, repository: UserPreferencesRepository) {
    val scope = rememberCoroutineScope()
    var managingProfiles by rememberSaveable { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<UserProfile?>(null) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    fun mutate(action: suspend () -> Unit) {
        if (saving) return
        saving = true
        error = null
        scope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { error = "No se pudo guardar el cambio. Inténtalo de nuevo." }
            finally { saving = false }
        }
    }
    val sessions = rememberSaveableStateHolder()
    val profile = preferences.activeProfile
    Column(Modifier.fillMaxSize()) {
        error?.let {
            Text(it, Modifier.safeDrawingPadding().testTag("runtime-save-error"), color = MaterialTheme.colorScheme.error)
        }
        if (saving) LinearProgressIndicator(Modifier.fillMaxWidth())
        Box(Modifier.weight(1f)) {
            when {
                profile == null -> FirstProfileScreen { name -> mutate { repository.createProfile(name) } }
                managingProfiles -> {
                    BackHandler { if (!saving) managingProfiles = false }
                    Column(Modifier.safeDrawingPadding().testTag("comunicago-profiles")) {
                        TextButton(onClick = { managingProfiles = false }, enabled = !saving) { Text("Volver a ComunicaGo") }
                        Box(Modifier.weight(1f)) {
                            ProfilesScreen(preferences.profiles, profile.id,
                                onCreateProfile = { name -> mutate { repository.createProfile(name) } },
                                onRenameProfile = { id, name -> mutate { repository.renameProfile(id, name) } },
                                onSelectProfile = { id -> mutate { repository.selectProfile(id); managingProfiles = false } },
                                profileSwitchingEnabled = !saving,
                            )
                        }
                        OutlinedButton(onClick = { pendingDelete = profile }, enabled = !saving,
                            modifier = Modifier.padding(16.dp).fillMaxWidth().heightIn(min = 48.dp)) {
                            Text("Eliminar perfil activo")
                        }
                    }
                }
                else -> sessions.SaveableStateProvider(profile.id) {
                    CommunicationProfileEntryScreen(profile.id, profile.name,
                        setupRepository = repository,
                        presentationRepository = repository,
                        vocabularyRepository = repository,
                        homeVocabularyRepository = repository,
                        speechPreferencesRepository = repository,
                        onBackToModules = { managingProfiles = true },
                        accessibilityContent = {
                            SectionHeading("Tema")
                            ThemeMode.entries.forEach { mode ->
                                CommunicationChoiceOption(
                                    when (mode) { ThemeMode.DAY -> "Día"; ThemeMode.NIGHT -> "Noche"; ThemeMode.SYSTEM -> "Sistema" },
                                    "theme-" + mode.name, preferences.themeMode == mode, !saving,
                                ) { mutate { repository.setThemeMode(mode) } }
                            }
                            SectionHeading("Tamaño del texto")
                            TextScaleMode.entries.forEach { mode ->
                                CommunicationChoiceOption(
                                    when (mode) { TextScaleMode.NORMAL -> "Normal"; TextScaleMode.LARGE -> "Grande"; TextScaleMode.VERY_LARGE -> "Muy grande" },
                                    "text-scale-" + mode.name, preferences.textScaleMode == mode, !saving,
                                ) { mutate { repository.setTextScaleMode(mode) } }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Alto contraste", Modifier.weight(1f))
                                Switch(preferences.highContrast, { enabled -> mutate { repository.setHighContrast(enabled) } },
                                    enabled = !saving, modifier = Modifier.testTag("high-contrast").semantics { contentDescription = "Alto contraste" })
                            }
                            Text("Preferencias de ComunicaGo guardadas en este dispositivo.")
                        },
                    )
                }
            }
        }
    }
    pendingDelete?.let { deleting ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar perfil") },
            text = { Text("Se eliminará " + deleting.name + " y sus preferencias de comunicación de este dispositivo.") },
            confirmButton = { TextButton(onClick = {
                pendingDelete = null
                mutate { repository.deleteProfile(deleting.id); sessions.removeState(deleting.id) }
            }, enabled = !saving) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") } },
        )
    }
}