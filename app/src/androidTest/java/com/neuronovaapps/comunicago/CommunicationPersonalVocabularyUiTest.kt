package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationPersonalVocabularyUiTest : StandaloneRuntimeTestBase() {
    @Test fun emptyVocabularyIsPersistedAndRecoverableFromAdministration() {
        open()
        runBlocking { repository.saveVocabulary(a, emptySet()) }
        rule.onNodeWithTag("communication-destination-MY_VOCABULARY").performClick()
        rule.onNodeWithTag("personal-vocabulary").assertExists()
        rule.activityRule.scenario.recreate(); open()
        runBlocking { assertEquals(emptySet<String>(), repository.vocabularyForProfile(a).first()) }
        rule.onNodeWithTag("communication-destination-MY_VOCABULARY").performClick()
        rule.onNodeWithTag("personal-vocabulary").performScrollToKey("search")
        rule.onNodeWithTag("vocabulary-search").performTextReplacement("VOC_015")
        rule.onNodeWithTag("personal-vocabulary").performScrollToKey("VOC_015")
        rule.onNodeWithTag("vocabulary-toggle-VOC_015").performScrollTo().performClick()
        rule.waitUntil(10_000) { runBlocking { "VOC_015" in repository.vocabularyForProfile(a).first().orEmpty() } }
    }
}
