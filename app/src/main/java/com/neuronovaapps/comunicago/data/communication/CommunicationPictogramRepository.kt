package com.neuronovaapps.comunicago.data.communication

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramDownloadWorker
import com.neuronovaapps.comunicago.data.arasaac.offline.PictogramFileStore
import com.neuronovaapps.comunicago.data.arasaac.offline.WorkManagerPictogramQueue
import com.neuronovaapps.comunicago.domain.communication.CommunicationPictogramStatus
import com.neuronovaapps.comunicago.domain.communication.validatedPictogramId
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class CommunicationPictogramImage(val status: CommunicationPictogramStatus, val file: File? = null)
interface CommunicationPictograms {
    fun observe(id: Long): Flow<CommunicationPictogramImage>
}
enum class PictogramTransfer { WAITING, RUNNING, FAILED, FINISHED }

/** Thin adapter over the existing unique work and PNG store. It never retrieves a URL. */
class CommunicationPictogramRepository(
    private val scope: CoroutineScope,
    private val localFile: (Long) -> File?,
    private val transfers: (Long) -> Flow<PictogramTransfer>,
    private val enqueue: (Long) -> Unit,
    private val allowed: suspend (Long) -> Boolean,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : CommunicationPictograms {
    private val observations = mutableMapOf<Long, Flow<CommunicationPictogramImage>>()

    @Synchronized override fun observe(id: Long): Flow<CommunicationPictogramImage> {
        require(id > 0)
        return observations.getOrPut(id) {
            flow {
                if (!allowed(id)) { emit(CommunicationPictogramImage(CommunicationPictogramStatus.PENDING)); return@flow }
                val cached = localFile(id)
                if (cached != null) { emit(CommunicationPictogramImage(CommunicationPictogramStatus.AVAILABLE, cached)); return@flow }
                emit(CommunicationPictogramImage(CommunicationPictogramStatus.NOT_STORED))
                val work = transfers(id)
                // Permanent failures require a later explicit retry policy, not a loop on recomposition.
                if (work.first() != PictogramTransfer.FAILED) enqueue(id)
                emitAll(work.map { transfer ->
                    val file = localFile(id)
                    CommunicationPictogramImage(when {
                        file != null -> CommunicationPictogramStatus.AVAILABLE
                        transfer == PictogramTransfer.RUNNING -> CommunicationPictogramStatus.LOADING
                        transfer in listOf(PictogramTransfer.FAILED, PictogramTransfer.FINISHED) -> CommunicationPictogramStatus.FAILED
                        else -> CommunicationPictogramStatus.NOT_STORED
                    }, file)
                })
            }.catch { error ->
                if (error is CancellationException) throw error
                emit(CommunicationPictogramImage(CommunicationPictogramStatus.FAILED))
            }.distinctUntilChanged().flowOn(io)
                .shareIn(scope, SharingStarted.WhileSubscribed(5_000), replay = 1)
        }
    }
}

/** Common bank references, never profile data. Loading this set does not download any image. */
object CommunicationPictogramReferences {
    @Volatile private var validatedIds: Set<Long>? = null
    suspend fun contains(context: Context, id: Long): Boolean = withContext(Dispatchers.IO) {
        val ids = validatedIds ?: synchronized(this@CommunicationPictogramReferences) {
            validatedIds ?: LocalCommunicationCatalogRepository {
                context.applicationContext.assets.open(LocalCommunicationCatalogRepository.ASSET_PATH)
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
            }.vocabulary.mapNotNull { it.validatedPictogramId() }.toSet().also { validatedIds = it }
        }
        id in ids
    }
}

object DefaultCommunicationPictograms {
    @Volatile private var instance: CommunicationPictograms? = null
    fun get(context: Context): CommunicationPictograms = instance ?: synchronized(this) {
        instance ?: create(context.applicationContext).also { instance = it }
    }
    private fun create(context: Context): CommunicationPictograms {
        val store = PictogramFileStore.fromFilesDir(context.filesDir)
        val work = WorkManager.getInstance(context)
        val queue = WorkManagerPictogramQueue(context)
        return CommunicationPictogramRepository(CoroutineScope(SupervisorJob() + Dispatchers.IO), store::getLocalFile,
            transfers = { id -> work.getWorkInfosForUniqueWorkFlow(PictogramDownloadWorker.uniqueName(id)).map { infos ->
                when {
                    infos.any { it.state == WorkInfo.State.RUNNING } -> PictogramTransfer.RUNNING
                    infos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED } -> PictogramTransfer.WAITING
                    infos.any { it.state == WorkInfo.State.FAILED || it.state == WorkInfo.State.CANCELLED } -> PictogramTransfer.FAILED
                    infos.any { it.state == WorkInfo.State.SUCCEEDED } -> PictogramTransfer.FINISHED
                    else -> PictogramTransfer.WAITING
                }
            } }, enqueue = queue::enqueueUnique, allowed = { CommunicationPictogramReferences.contains(context, it) })
    }
}
