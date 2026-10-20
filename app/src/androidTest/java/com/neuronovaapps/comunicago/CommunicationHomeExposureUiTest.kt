package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationHomeExposureUiTest : StandaloneRuntimeTestBase() {
    @Test fun bothPresentationsExposeSearchAndHomeOrganization() {
        open()
        CommunicationPresentationMode.entries.forEach { mode ->
            runBlocking { repository.setPresentationMode(a, mode) }
            rule.onNodeWithTag("communication-destination-HOME").performClick()
            rule.onNodeWithTag("communication-home").performScrollToKey("explore")
            rule.onNodeWithTag("communication-explore").assertIsDisplayed()
            rule.onNodeWithTag("communication-organize-home").assertIsDisplayed()
        }
    }
}
