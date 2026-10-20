package com.neuronovaapps.comunicago

import com.neuronovaapps.comunicago.data.arasaac.offline.IncompletePictogramException
import com.neuronovaapps.comunicago.data.arasaac.offline.InvalidPictogramException
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramDownloadResult
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramDownloadTask
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramDownloader
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramFileStore
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramStorageException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.concurrent.TimeUnit
import java.util.zip.CRC32
import java.util.zip.DeflaterOutputStream
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PictogramOfflineTest {
    @get:Rule val temporary = TemporaryFolder()
    private lateinit var server: MockWebServer
    private lateinit var store: PictogramFileStore
    private lateinit var downloader: PictogramDownloader

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        store = PictogramFileStore.fromFilesDir(temporary.root)
        downloader = PictogramDownloader(store, imageUrl = { id ->
            server.url("/pictograms/$id/${id}_500.png").toString()
        })
    }

    @After fun tearDown() { server.close() }

    @Test fun stablePrivatePathAndInvalidId() {
        assertEquals("pictograms/arasaac/10384.png",
            store.fileFor(10384).relativeTo(temporary.root).invariantSeparatorsPath)
        assertThrows(IllegalArgumentException::class.java) { store.fileFor(0) }
        assertThrows(IllegalArgumentException::class.java) { store.fileFor(-2) }
    }

    @Test fun taskDownloadsOnlyPersistedMissingIds() = runBlocking {
        var calls = 0
        val unreferenced = PictogramDownloadTask({ false }, { false }, { calls++; PictogramDownloadResult.READY })
        assertEquals(PictogramDownloadResult.READY, unreferenced.run(10384))
        val local = PictogramDownloadTask({ true }, { true }, { calls++; PictogramDownloadResult.READY })
        assertEquals(PictogramDownloadResult.READY, local.run(10384))
        val missing = PictogramDownloadTask({ true }, { false }, { calls++; PictogramDownloadResult.RETRY })
        assertEquals(PictogramDownloadResult.RETRY, missing.run(10384))
        assertEquals(1, calls)
    }

    @Test fun validDownloadUsesIdOnlyAndStoresPngOnce() = runBlocking {
        val png = png()
        server.enqueue(pngResponse(png))
        assertEquals(PictogramDownloadResult.READY, downloader.download(10384))
        val request = server.takeRequest()
        assertEquals("/pictograms/10384/10384_500.png", request.url.encodedPath)
        assertEquals("image/png", request.headers["Accept"])
        assertArrayEquals(png, store.getLocalFile(10384)?.readBytes())
        assertArrayEquals(png, PictogramFileStore.fromFilesDir(temporary.root)
            .getLocalFile(10384)?.readBytes())
        assertEquals(1, store.stats().fileCount)
        assertEquals(png.size.toLong(), store.stats().totalBytes)
        assertEquals(PictogramDownloadResult.READY, downloader.download(10384))
        assertNull(server.takeRequest(50, TimeUnit.MILLISECONDS))
        assertTrue(store.fileFor(10384).parentFile!!.listFiles().orEmpty()
            .none { it.name.endsWith(".part") })
    }

    @Test fun twoUsesOfSameIdShareAFileAndExistingFileAvoidsNetwork() = runBlocking {
        val png = png()
        val first = store.save(6456, ByteArrayInputStream(png), png.size.toLong())
        val second = store.save(6456, ByteArrayInputStream(png), png.size.toLong())
        assertEquals(first, second)
        assertEquals(1, store.stats().fileCount)
        assertEquals(PictogramDownloadResult.READY, downloader.download(6456))
        assertNull(server.takeRequest(50, TimeUnit.MILLISECONDS))
    }

    @Test fun truncatedAndMalformedPngNeverBecomeFinalFiles() {
        val png = png()
        assertThrows(IncompletePictogramException::class.java) {
            store.save(1, ByteArrayInputStream(png), png.size.toLong() + 1)
        }
        assertFalse(store.exists(1))
        assertThrows(InvalidPictogramException::class.java) {
            store.save(2, ByteArrayInputStream("not png".toByteArray()))
        }
        assertFalse(store.exists(2))
        assertThrows(InvalidPictogramException::class.java) {
            store.save(3, ByteArrayInputStream(ByteArray(PictogramFileStore.MAX_BYTES.toInt() + 1)))
        }
        assertFalse(store.exists(3))
        assertEquals(0, store.stats().fileCount)
        assertTrue(store.fileFor(1).parentFile!!.listFiles().orEmpty()
            .none { it.name.endsWith(".part") })
    }

    @Test fun unwritableDirectoryReportsStorageFailureWithoutFinalFile() {
        val occupied = temporary.newFile("occupied")
        val blocked = PictogramFileStore(java.io.File(occupied, "arasaac"))
        val png = png()
        assertThrows(PictogramStorageException::class.java) {
            blocked.save(10384, ByteArrayInputStream(png), png.size.toLong())
        }
        assertFalse(blocked.exists(10384))
    }

    @Test fun downloaderTreatsStorageFailureAsTerminalWhileReferenceCanRemain() = runBlocking {
        val occupied = temporary.newFile("storage-occupied")
        val blocked = PictogramFileStore(java.io.File(occupied, "arasaac"))
        server.enqueue(pngResponse(png()))
        val blockedDownloader = PictogramDownloader(blocked, imageUrl = { id ->
            server.url("/pictograms/$id.png").toString()
        })
        assertEquals(PictogramDownloadResult.PERMANENT_FAILURE, blockedDownloader.download(10384))
        assertFalse(blocked.exists(10384))
    }

    @Test fun emptyWrongTypeAndOversizedResponsesArePermanentFailures() = runBlocking {
        server.enqueue(MockResponse.Builder().addHeader("Content-Type", "image/png").body("").build())
        server.enqueue(MockResponse.Builder().addHeader("Content-Type", "text/html").body("error").build())
        server.enqueue(MockResponse.Builder().addHeader("Content-Type", "image/png")
            .body(Buffer().write(ByteArray(PictogramFileStore.MAX_BYTES.toInt() + 1))).build())
        assertEquals(PictogramDownloadResult.PERMANENT_FAILURE, downloader.download(11))
        assertEquals(PictogramDownloadResult.PERMANENT_FAILURE, downloader.download(12))
        assertEquals(PictogramDownloadResult.PERMANENT_FAILURE, downloader.download(13))
        assertEquals(0, store.stats().fileCount)
    }

    @Test fun notFoundIsPermanentWhileRateLimitAndServerFailureRetry() = runBlocking {
        server.enqueue(MockResponse.Builder().code(404).build())
        server.enqueue(MockResponse.Builder().code(429).addHeader("Retry-After", "15").build())
        server.enqueue(MockResponse.Builder().code(503).build())
        assertEquals(PictogramDownloadResult.PERMANENT_FAILURE, downloader.download(21))
        assertEquals(PictogramDownloadResult.RETRY, downloader.download(22))
        assertEquals(PictogramDownloadResult.RETRY, downloader.download(23))
        assertEquals(0, store.stats().fileCount)
    }

    @Test fun timeoutRetriesAndLeavesNoFinalFile() = runBlocking {
        server.enqueue(MockResponse.Builder().addHeader("Content-Type", "image/png")
            .body(Buffer().write(png())).bodyDelay(250, TimeUnit.MILLISECONDS).build())
        val client = OkHttpClient.Builder().readTimeout(40, TimeUnit.MILLISECONDS)
            .callTimeout(100, TimeUnit.MILLISECONDS).build()
        val slow = PictogramDownloader(store, client) { id -> server.url("/pictograms/$id.png").toString() }
        assertEquals(PictogramDownloadResult.RETRY, slow.download(31))
        assertFalse(store.exists(31))
    }

    @Test fun existingCorruptFinalIsReplacedOnlyAfterValidDownload() = runBlocking {
        val target = store.fileFor(44)
        target.parentFile!!.mkdirs()
        target.writeText("corrupt")
        assertNull(store.getLocalFile(44))
        server.enqueue(pngResponse(png()))
        assertEquals(PictogramDownloadResult.READY, downloader.download(44))
        assertNotNull(store.getLocalFile(44))
        assertEquals(1, store.stats().fileCount)
    }

    private fun pngResponse(png: ByteArray): MockResponse = MockResponse.Builder()
        .addHeader("Content-Type", "image/png")
        .body(Buffer().write(png)).build()

    private fun png(): ByteArray {
        val bytes = ByteArrayOutputStream()
        val output = DataOutputStream(bytes)
        output.write(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))
        val header = ByteArrayOutputStream().also { raw ->
            DataOutputStream(raw).use { data ->
                data.writeInt(1); data.writeInt(1)
                data.writeByte(8); data.writeByte(6)
                data.writeByte(0); data.writeByte(0); data.writeByte(0)
            }
        }.toByteArray()
        output.chunk("IHDR", header)
        val compressed = ByteArrayOutputStream().also { raw ->
            DeflaterOutputStream(raw).use { it.write(byteArrayOf(0, 255.toByte(), 0, 0, 255.toByte())) }
        }.toByteArray()
        output.chunk("IDAT", compressed)
        output.chunk("IEND", byteArrayOf())
        return bytes.toByteArray()
    }

    private fun DataOutputStream.chunk(type: String, payload: ByteArray) {
        val name = type.toByteArray(Charsets.US_ASCII)
        writeInt(payload.size); write(name); write(payload)
        val crc = CRC32().also { it.update(name); it.update(payload) }
        writeInt(crc.value.toInt())
    }

}
