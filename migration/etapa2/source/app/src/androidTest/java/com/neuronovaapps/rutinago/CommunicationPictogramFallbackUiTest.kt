package com.neuronovaapps.rutinago

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.neuronovaapps.rutinago.data.communication.*
import com.neuronovaapps.rutinago.domain.communication.*
import com.neuronovaapps.rutinago.ui.screens.communication.components.*
import com.neuronovaapps.rutinago.ui.theme.RutinaGoTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunicationPictogramFallbackUiTest {
    @get:Rule val rule = createComposeRule(effectContext = StandardTestDispatcher())

    @Test fun all132CardsRemainUsableOfflineAndOnly119ApprovedWordsRequestImages() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = LocalCommunicationCatalogRepository {
            context.assets.open(LocalCommunicationCatalogRepository.ASSET_PATH).bufferedReader().use { it.readText() }
        }
        var index by mutableIntStateOf(0)
        var attempts = 0
        val images = object : CommunicationPictograms {
            override fun observe(id: Long): Flow<CommunicationPictogramImage> {
                attempts++
                return flowOf(CommunicationPictogramImage(CommunicationPictogramStatus.NOT_STORED))
            }
        }
        val builder = CommunicationMessageBuilder(catalog)
        var message = builder.emptyState()
        rule.setContent {
            RutinaGoTheme {
                CompositionLocalProvider(LocalCommunicationPictograms provides images) {
                    val word = catalog.vocabulary[index]
                    CommunicationVocabularyCard(word, { message = builder.add(message, it) })
                }
            }
        }
        for ((position, word) in catalog.vocabulary.withIndex()) {
            rule.runOnIdle { index = position }
            rule.onNodeWithTag("communication-word-${word.vocabId}")
                .assertIsDisplayed().assertTextEquals(word.text).assertHeightIsAtLeast(48.dp).performClick()
            val image = rule.onNodeWithTag("communication-pictogram-${word.vocabId}", useUnmergedTree = true)
            if (word.validatedPictogramId() == null) image.assertDoesNotExist() else image.assertExists()
            assertEquals(word.text, message.messageItems.last().text)
        }
        assertEquals(132, message.messageItems.size)
        assertEquals(catalog.vocabulary.map { it.text }.joinToString(" "), communicationSpeechText(message.messageItems))
        assertEquals(119, attempts)
        assertTrue(CommunicationPictogramDisplayReview.approved)
    }
}
