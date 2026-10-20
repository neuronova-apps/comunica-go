package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationBottomNavigationUiTest : StandaloneRuntimeTestBase() {
    @Test fun standaloneTabsAndRecreationKeepMessage() {
        open(); word("VOC_006")
        CommunicationDestination.entries.forEach {
            rule.onNodeWithTag("communication-destination-" + it.name).performClick().assertIsSelected()
            rule.onNodeWithTag("module-selector").assertDoesNotExist()
        }
        rule.activityRule.scenario.recreate(); open(); message("Quiero")
    }
}
