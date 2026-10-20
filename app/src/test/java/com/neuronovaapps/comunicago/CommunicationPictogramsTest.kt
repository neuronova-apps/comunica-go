package com.neuronovaapps.comunicago

import com.neuronovaapps.comunicago.data.communication.*
import com.neuronovaapps.comunicago.domain.communication.*
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunicationPictogramsTest {
    private val root = generateSequence(File(requireNotNull(System.getProperty("user.dir"))).absoluteFile) { it.parentFile }
        .first { File(it, "app/src/main/assets/communication/communication_catalog_v1.json").isFile }
    private val asset = File(root, "app/src/main/assets/communication/communication_catalog_v1.json")
    private val catalog = LocalCommunicationCatalogRepository { asset.readText() }
    private val water get() = catalog.vocabulary.single { it.vocabId == "VOC_015" }

    @Test fun onlyThe119CuratedWordsHaveImageIds() {
        assertEquals(119, catalog.vocabulary.count { it.validatedPictogramId() != null })
        assertEquals(2248L, water.validatedPictogramId())
        assertEquals(132, catalog.vocabulary.size); assertEquals(18, catalog.categories.size)
    }
    @Test fun textOnlyWordsNeverProduceAnImageRequest() {
        val words = catalog.vocabulary.filter { it.visualStrategy == "SOLO_TEXTO" }
        assertEquals(13, words.size)
        words.forEach { assertNull(it.validatedPictogramId()); assertEquals(CommunicationPictogramStatus.TEXT_ONLY, it.pictogramFallback()) }
    }
    @Test fun pendingPictogramsRemainPendingEvenWithAnUnapprovedId() {
        val words = listOf(water.copy(status = "PENDIENTE_ARASAAC", visualStrategy = "PICTOGRAMA_PENDIENTE"))
        assertTrue(catalog.vocabulary.none { it.visualStrategy == "PICTOGRAMA_PENDIENTE" })
        words.forEach { assertNull(it.copy(arasaacId = "2248").validatedPictogramId()); assertEquals(CommunicationPictogramStatus.PENDING, it.pictogramFallback()) }
    }
    @Test fun missingInvalidOrUnvalidatedIdsDoNotBecomeApproximateMatches() {
        listOf("", "0", "-1", "not-an-id", "999999999999999999999").forEach { assertNull(water.copy(arasaacId = it).validatedPictogramId()) }
        assertNull(water.copy(status = "PENDIENTE_ARASAAC").validatedPictogramId())
        assertNull(water.copy(visualStrategy = "SOLO_TEXTO").validatedPictogramId())
    }
    @Test fun mappingNeverChangesTheBankMessageRepetitionsOrTtsText() {
        val original = asset.readBytes(); val builder = CommunicationMessageBuilder(catalog)
        var message = builder.emptyState()
        for (word in listOf(water, catalog.vocabulary.first { it.visualStrategy == "SOLO_TEXTO" }, water)) {
            word.validatedPictogramId(); message = builder.add(message, word.vocabId)
        }
        val restored = CommunicationStateCodec.decode(CommunicationStateCodec.encode(message), builder)
        assertEquals(message.messageItems, restored.messageItems)
        assertEquals(message.messageItems.joinToString(" ") { it.text }, communicationSpeechText(restored.messageItems))
        assertEquals("Agua", restored.messageItems.first().text); assertEquals("Agua", restored.messageItems.last().text)
        assertArrayEquals(original, asset.readBytes())
    }
    @Test fun homeAndVisibleSelectionRemainIndependentFromImageAvailability() {
        val available = setOf(water.vocabId, "VOC_001")
        assertEquals(listOf(water.vocabId), CommunicationHomeVocabulary.words(catalog, setOf(water.vocabId), available).map { it.vocabId })
        assertTrue(CommunicationHomeVocabulary.words(catalog, setOf(water.vocabId), available - water.vocabId).isEmpty())
        assertEquals(119, catalog.vocabulary.mapNotNull { it.validatedPictogramId() }.size)
    }

    private class Fixture(scope: CoroutineScope, dispatcher: CoroutineDispatcher) {
        var file: File? = null
        var permitted = true
        var localReads = 0
        var workReads = 0
        var enqueues = 0
        val transfer = MutableStateFlow(PictogramTransfer.WAITING)
        val repo = CommunicationPictogramRepository(scope, { localReads++; file },
            { workReads++; transfer }, { enqueues++ }, { permitted }, dispatcher)
    }
    @Test fun validLocalFileWinsWithoutConsultingOrEnqueuingWork() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); f.file = File("existing.png")
        val result = async { f.repo.observe(2248).first() }; runCurrent()
        assertEquals(CommunicationPictogramStatus.AVAILABLE, result.await().status)
        assertEquals(f.file, result.await().file); assertEquals(0, f.enqueues); assertEquals(0, f.workReads)
    }
    @Test fun absentImageAndOfflineWaitingRemainTextFallback() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); val states = mutableListOf<CommunicationPictogramImage>()
        backgroundScope.launch { f.repo.observe(2248).toList(states) }; runCurrent()
        assertEquals(1, f.enqueues); assertEquals(CommunicationPictogramStatus.NOT_STORED, states.last().status)
        assertNull(states.last().file)
    }
    @Test fun runningAndFailedTransfersAreDistinctWithoutThrowingToTheUi() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); val states = mutableListOf<CommunicationPictogramImage>()
        backgroundScope.launch { f.repo.observe(2248).toList(states) }; runCurrent()
        f.transfer.value = PictogramTransfer.RUNNING; runCurrent()
        assertEquals(CommunicationPictogramStatus.LOADING, states.last().status)
        f.transfer.value = PictogramTransfer.FAILED; runCurrent()
        assertEquals(CommunicationPictogramStatus.FAILED, states.last().status); assertEquals(1, f.enqueues)
    }
    @Test fun simultaneousObserversAndProfilesShareTheSameWorkAndFlow() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler))
        assertSame(f.repo.observe(2248), f.repo.observe(2248))
        repeat(8) { backgroundScope.launch { f.repo.observe(2248).collect() } }; runCurrent()
        assertEquals(1, f.enqueues)
    }
    @Test fun existingPermanentFailureDoesNotStartARecompositionRetryLoop() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); f.transfer.value = PictogramTransfer.FAILED
        val result = async { f.repo.observe(2248).first { it.status == CommunicationPictogramStatus.FAILED } }; runCurrent()
        assertEquals(CommunicationPictogramStatus.FAILED, result.await().status); assertEquals(0, f.enqueues)
    }
    @Test fun completedDownloadIsPublishedAndReusedAfterLeavingTheModule() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); val states = mutableListOf<CommunicationPictogramImage>()
        val observer = backgroundScope.launch { f.repo.observe(2248).toList(states) }; runCurrent()
        f.file = File("shared.png"); f.transfer.value = PictogramTransfer.FINISHED; runCurrent()
        assertEquals(CommunicationPictogramStatus.AVAILABLE, states.last().status)
        observer.cancel(); advanceTimeBy(5_001); runCurrent()
        val again = async { f.repo.observe(2248).first() }; runCurrent()
        assertEquals(f.file, again.await().file); assertEquals(1, f.enqueues)
    }
    @Test fun successWithoutAValidFileRemainsAFailureWithTextAvailable() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); f.transfer.value = PictogramTransfer.FINISHED
        val result = async { f.repo.observe(2248).first { it.status == CommunicationPictogramStatus.FAILED } }; runCurrent()
        assertNull(result.await().file)
    }
    @Test fun unapprovedIdNeverReadsAFileOrEnqueuesARequest() = runTest {
        val f = Fixture(backgroundScope, StandardTestDispatcher(testScheduler)); f.permitted = false
        val result = async { f.repo.observe(42).first() }; runCurrent()
        assertEquals(CommunicationPictogramStatus.PENDING, result.await().status)
        assertEquals(0, f.localReads); assertEquals(0, f.enqueues); assertEquals(0, f.workReads)
    }
    @Test fun storageErrorsBecomeFallbackAndNeverCrashTheCollector() = runTest {
        val repo = CommunicationPictogramRepository(backgroundScope, { error("storage failure") },
            { flowOf(PictogramTransfer.WAITING) }, { error("must not enqueue") }, { true }, StandardTestDispatcher(testScheduler))
        val result = async { repo.observe(2248).first() }; runCurrent()
        assertEquals(CommunicationPictogramStatus.FAILED, result.await().status)
    }
}
