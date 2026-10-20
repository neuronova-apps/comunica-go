package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.neuronovaapps.comunicago.data.communication.LocalCommunicationCatalogRepository
import com.neuronovaapps.comunicago.data.preferences.*
import com.neuronovaapps.comunicago.domain.communication.*
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*

abstract class StandaloneRuntimeTestBase {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    protected lateinit var repository: UserPreferencesRepository
    protected lateinit var original: UserPreferences
    protected val a = "runtime-a-" + UUID.randomUUID()
    protected val b = "runtime-b-" + UUID.randomUUID()
    @Before fun seedStandaloneProfiles() = runBlocking {
        repository = UserPreferencesRepository(rule.activity)
        original = repository.preferences.first()
        for (id in listOf(a,b)) {
            repository.createProfile(if (id == a) "Runtime A" else "Runtime B", id)
            repository.saveSetup(CommunicationProfileSetup(id, CommunicationAgeRange.NOT_SPECIFIED, OralCommunication.NOT_SPECIFIED))
            repository.saveVocabulary(id, setOf("VOC_001","VOC_006","VOC_014","VOC_015"))
        }
        repository.selectProfile(a)
    }
    @After fun cleanStandaloneProfiles(): Unit = runBlocking {
        for (id in listOf(a,b)) if (repository.preferences.first().profiles.any { it.id == id }) repository.deleteProfile(id)
        original.activeProfile?.id?.let { repository.selectProfile(it) }
        repository.setThemeMode(original.themeMode)
        repository.setTextScaleMode(original.textScaleMode)
        repository.setHighContrast(original.highContrast)
    }
    protected fun open() {
        rule.waitForIdle()
        if (rule.onAllNodesWithTag("welcome-start").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("welcome-start").performClick()
        }
        rule.awaitCommunicationCatalog()
    }
    protected fun word(id: String) {
        rule.scrollCommunicationTo("core-" + id)
        rule.onNodeWithTag("communication-core-" + id).performClick()
    }
    protected fun message(text: String) {
        rule.scrollCommunicationTo("message")
        rule.onNodeWithTag("communication-message-item-1").assertTextEquals(text)
    }
}
