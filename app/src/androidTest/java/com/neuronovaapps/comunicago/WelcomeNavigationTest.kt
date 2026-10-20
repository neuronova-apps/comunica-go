package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WelcomeNavigationTest : StandaloneRuntimeTestBase() {
    @Test fun launchShowsWelcomeAndStartEntersExistingHomeWithoutChangingPreferences() {
        val before = runBlocking { repository.preferences.first() }
        val vocabulary = runBlocking { repository.vocabularyForProfile(a).first() }
        rule.onNodeWithTag("comunicago-welcome").assertIsDisplayed()
        rule.onNodeWithText("ComunicaGo").assertIsDisplayed()
        rule.onNodeWithText("NeuroNova Apps").assertIsDisplayed()
        rule.onNodeWithTag("communication-bottom-bar").assertDoesNotExist()
        open()
        rule.onNodeWithTag("communication-destination-HOME").assertIsSelected()
        rule.onNodeWithTag("comunicago-welcome").assertDoesNotExist()
        rule.onNodeWithTag("module-selector").assertDoesNotExist()
        runBlocking {
            assertEquals(before, repository.preferences.first())
            assertEquals(vocabulary, repository.vocabularyForProfile(a).first())
        }
    }

    @Test fun recreationBeforeAndAfterStartKeepsTheCorrectScreenAndMessage() {
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("welcome-start").assertIsDisplayed()
        open()
        word("VOC_006")
        rule.activityRule.scenario.recreate()
        rule.awaitCommunicationCatalog()
        rule.onNodeWithTag("comunicago-welcome").assertDoesNotExist()
        message("Quiero")
    }

    @Test fun backAfterStartPreservesTheExistingProfilesAndCommunicatorFlow() {
        open()
        word("VOC_006")
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag("comunicago-profiles").assertIsDisplayed()
        rule.onNodeWithTag("comunicago-welcome").assertDoesNotExist()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.awaitCommunicationCatalog()
        message("Quiero")
    }
}
