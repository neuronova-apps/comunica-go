package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationPresentationUiTest : StandaloneRuntimeTestBase() {
    @Test fun presentationAndVoiceRateRemainIndependentPerProfile() {
        runBlocking {
            repository.setPresentationMode(a, CommunicationPresentationMode.FULL)
            repository.setSpeechRate(a, CommunicationSpeechRate.entries.first())
            repository.setPresentationMode(b, CommunicationPresentationMode.GUIDED)
        }
        open()
        rule.scrollCommunicationTo("presentation")
        rule.onNodeWithTag("communication-presentation-FULL").assertIsSelected()
        runBlocking {
            assertEquals(CommunicationPresentationMode.GUIDED, repository.presentationForProfile(b).first().selectedMode)
            assertEquals(CommunicationSpeechRate.NORMAL, repository.speechRateForProfile(b).first())
        }
    }
}
