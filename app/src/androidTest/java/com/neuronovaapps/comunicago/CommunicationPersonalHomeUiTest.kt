package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationPersonalHomeUiTest : StandaloneRuntimeTestBase() {
    @Test fun hiddenAssignmentSurvivesRecoveryAndRestart() {
        runBlocking { repository.setHomeWord(a, "VOC_015", true) }
        open(); word("VOC_015")
        runBlocking { repository.saveVocabulary(a, setOf("VOC_001")) }
        rule.waitUntil(10_000) { rule.onAllNodesWithTag("communication-core-VOC_015").fetchSemanticsNodes().isEmpty() }
        runBlocking {
            assertTrue("VOC_015" in requireNotNull(repository.homeVocabularyForProfile(a).first()))
            repository.saveVocabulary(a, setOf("VOC_001","VOC_015"))
        }
        rule.activityRule.scenario.recreate(); open()
        rule.scrollCommunicationTo("core-VOC_015")
        rule.onNodeWithTag("communication-core-VOC_015").assertIsDisplayed()
        message("Agua")
    }
}
