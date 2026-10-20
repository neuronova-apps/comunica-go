package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import com.neuronovaapps.comunicago.domain.communication.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
class CommunicationProfileSetupUiTest : StandaloneRuntimeTestBase() {
    @Test fun renameKeepsIdAndDeletionCleansFiveContextRecords() {
        open()
        runBlocking {
            repository.setPresentationMode(a, CommunicationPresentationMode.FULL)
            repository.setHomeWord(a, "VOC_015", true)
            repository.setSpeechRate(a, CommunicationSpeechRate.entries.first())
            repository.renameProfile(a, "Renombrado")
            assertEquals(a, repository.preferences.first().activeProfile?.id)
            repository.deleteProfile(a)
            assertNull(repository.setupForProfile(a).first())
            assertNull(repository.vocabularyForProfile(a).first())
            assertNull(repository.homeVocabularyForProfile(a).first())
            assertNull(repository.presentationForProfile(a).first().selectedMode)
            assertEquals(CommunicationSpeechRate.NORMAL, repository.speechRateForProfile(a).first())
            assertNotNull(repository.setupForProfile(b).first())
        }
    }
}
