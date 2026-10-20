package com.neuronovaapps.comunicago.data.arasaac.offline

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

interface PictogramWorkQueue {
    suspend fun hasFailedWork(id: Long): Boolean
    fun enqueueUnique(id: Long)
}

class WorkManagerPictogramQueue(context: Context) : PictogramWorkQueue {
    private val workManager = WorkManager.getInstance(context.applicationContext)

    override suspend fun hasFailedWork(id: Long): Boolean =
        workManager.getWorkInfosForUniqueWorkFlow(PictogramDownloadWorker.uniqueName(id))
            .first().any { it.state == WorkInfo.State.FAILED }

    override fun enqueueUnique(id: Long) {
        workManager.enqueueUniqueWork(
            PictogramDownloadWorker.uniqueName(id),
            ExistingWorkPolicy.KEEP,
            requestFor(id),
        )
    }

    companion object {
        internal fun requestFor(id: Long): OneTimeWorkRequest {
            require(id > 0)
            return OneTimeWorkRequestBuilder<PictogramDownloadWorker>()
                .setInputData(workDataOf(PictogramDownloadWorker.KEY_ARASAAC_ID to id))
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
        }
    }
}
