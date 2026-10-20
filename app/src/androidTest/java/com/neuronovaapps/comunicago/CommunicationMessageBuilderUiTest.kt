package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationMessageBuilderUiTest : StandaloneRuntimeTestBase() {
    @Test fun repeatRemoveClearUndoAndRecreation() {
        open(); word("VOC_006"); word("VOC_006")
        rule.scrollCommunicationTo("message")
        rule.onNodeWithTag("communication-message-item-2").assertTextEquals("Quiero")
        rule.onNodeWithTag("communication-remove-last").performScrollTo().performClick()
        rule.onNodeWithTag("communication-clear").performScrollTo().performClick()
        rule.activityRule.scenario.recreate(); open()
        rule.scrollCommunicationTo("message")
        rule.onNodeWithTag("communication-undo-clear").performScrollTo().performClick()
        message("Quiero")
    }
}
