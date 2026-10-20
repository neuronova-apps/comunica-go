package com.neuronovaapps.rutinago

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.neuronovaapps.rutinago.data.arasaac.offline.*
import com.neuronovaapps.rutinago.data.communication.*
import com.neuronovaapps.rutinago.data.preferences.TextScaleMode
import com.neuronovaapps.rutinago.domain.communication.*
import com.neuronovaapps.rutinago.ui.screens.communication.CommunicationHomeScreen
import com.neuronovaapps.rutinago.ui.screens.communication.components.*
import com.neuronovaapps.rutinago.ui.theme.RutinaGoTheme
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CommunicationCuratedPictogramsUiTest {
    @get:Rule val rule = createComposeRule()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val catalog get() = LocalCommunicationCatalogRepository {
        context.assets.open(LocalCommunicationCatalogRepository.ASSET_PATH).bufferedReader().use { it.readText() }
    }
    private fun evidenceFile(): File = File(context.cacheDir, "curated-water-test.png").apply {
        instrumentation.context.assets.open("arasaac/2248_500.png").use { input -> outputStream().use { input.copyTo(it) } }
    }
    private fun hash(file: File) = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }
    private fun capture(name: String) {
        rule.waitForIdle()
        val folder = File(context.getExternalFilesDir(null), "curated-evidence").apply { mkdirs() }
        File(folder, "$name.txt").writeText(rule.onRoot(useUnmergedTree = true).printToString())
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(folder, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    /** Real network request using the existing downloader, isolated from personal/shared cache. */
    @Test fun authenticArasaacDownloadMatchesAuditedWaterAndWorkerReusesSharedCache() = runBlocking {
        val folder = File(context.cacheDir, "arasaac-authentic-${System.nanoTime()}").apply { mkdirs() }
        val isolated = PictogramFileStore(folder)
        assertEquals(PictogramDownloadResult.READY, PictogramDownloader(isolated).download(2248))
        val actual = requireNotNull(isolated.getLocalFile(2248))
        assertEquals(hash(evidenceFile()), hash(actual))
        val decoded = requireNotNull(BitmapFactory.decodeFile(actual.path))
        assertEquals(500, decoded.width); assertEquals(500, decoded.height); decoded.recycle()
        // The production store is the same location used by RutinaGo, never a module cache.
        val shared = PictogramFileStore.fromFilesDir(context.filesDir)
        actual.inputStream().use { shared.save(2248, it, actual.length()) }
        assertTrue(CommunicationPictogramReferences.contains(context, 2248))
        assertFalse(CommunicationPictogramReferences.contains(context, 7157))
        val worker = TestListenableWorkerBuilder<PictogramDownloadWorker>(context)
            .setInputData(workDataOf(PictogramDownloadWorker.KEY_ARASAAC_ID to 2248L)).build()
        assertTrue(worker.doWork() is ListenableWorker.Result.Success)
        val restartedStore = PictogramFileStore.fromFilesDir(context.filesDir)
        assertEquals(hash(actual), hash(requireNotNull(restartedStore.getLocalFile(2248))))
        assertEquals(PictogramDownloadResult.READY, PictogramDownloader(restartedStore,
            imageUrl = { error("Cached image must not request a URL, including after restart") }).download(2248))
        File(context.getExternalFilesDir(null), "curated-evidence").apply { mkdirs() }
            .resolve("authentic-download.txt").writeText("REAL HTTP: https://static.arasaac.org/pictograms/2248/2248_500.png\nSHA256=${hash(actual)}\nPNG=500x500\nshared-store=${restartedStore.fileFor(2248)}\n")
        actual.delete(); folder.delete() // Only this test's isolated temporary artifact.
        Unit
    }

    private data class Scenario(val mode: CommunicationPresentationMode, val appearance: Int, val scale: Float)
    /** Saved authentic fixture + controlled transfer states; this is not a live-download UI test. */
    @Test fun approvedImageAndTextStayAccessibleAcrossModesThemesScalesAndTransferFailures() {
        val file = evidenceFile()
        val transfer = MutableStateFlow(CommunicationPictogramImage(CommunicationPictogramStatus.AVAILABLE, file))
        val images = object : CommunicationPictograms { override fun observe(id: Long) = transfer }
        var scenario by mutableStateOf(Scenario(CommunicationPresentationMode.GUIDED, 0, 1f))
        rule.setContent {
            val s = scenario
            val content: @Composable () -> Unit = {
                CompositionLocalProvider(LocalCommunicationPictograms provides images) {
                    key(s) { CommunicationHomeScreen("Perfil de prueba", presentationMode = s.mode,
                        visibleVocabularyIds = setOf("VOC_015", "VOC_097"), homeVocabularyIds = setOf("VOC_015", "VOC_097"), onBackToModules = {}) }
                }
            }
            if (s.appearance == 2) RutinaGoTheme(textScale = s.scale, content = content)
            else RutinaGoTheme(darkTheme = s.appearance == 1, highContrast = s.appearance == 3, textScale = s.scale, content = content)
        }
        for (mode in CommunicationPresentationMode.entries) for (appearance in 0..3) for (scale in TextScaleMode.entries.map { it.scaleFactor }) {
            rule.runOnIdle { scenario = Scenario(mode, appearance, scale); transfer.value = CommunicationPictogramImage(CommunicationPictogramStatus.AVAILABLE, file) }
            rule.awaitCommunicationCatalog()
            rule.scrollCommunicationTo("core-VOC_015")
            rule.waitUntil(15_000) {
                rule.onAllNodesWithTag("communication-pictogram-VOC_015", useUnmergedTree = true).fetchSemanticsNodes()
                    .any { it.config.contains(PictogramStatusKey) && it.config[PictogramStatusKey] == CommunicationPictogramStatus.AVAILABLE }
            }
            val card = rule.onNodeWithTag("communication-core-VOC_015")
            card.assertIsDisplayed().assertHasClickAction().assertHeightIsAtLeast(48.dp).assertTextEquals("Agua")
            rule.onNodeWithTag("communication-pictogram-VOC_015", useUnmergedTree = true)
                .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
            val imageHeight = rule.onNodeWithTag("communication-pictogram-VOC_015", useUnmergedTree = true).getUnclippedBoundsInRoot().let { it.bottom - it.top }
            if (scale == TextScaleMode.VERY_LARGE.scaleFactor && appearance in listOf(0, 3)) capture("${mode.name}-$appearance-large")
            card.performClick()
            rule.runOnIdle { transfer.value = CommunicationPictogramImage(CommunicationPictogramStatus.FAILED) }
            rule.onNodeWithTag("communication-core-VOC_015").assertTextEquals("Agua").assertHasClickAction()
            assertEquals(imageHeight, rule.onNodeWithTag("communication-pictogram-VOC_015", useUnmergedTree = true).getUnclippedBoundsInRoot().let { it.bottom - it.top })
            rule.scrollCommunicationTo("core-VOC_097")
            rule.onNodeWithTag("communication-core-VOC_097").assertTextEquals("Ruido").performClick()
            rule.onNodeWithTag("communication-pictogram-VOC_097", useUnmergedTree = true).assertDoesNotExist()
        }
        file.delete()
    }
}
