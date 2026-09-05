package com.docscanner.app.service.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.docscanner.app.domain.service.cloud.CloudStorageService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudStorageService: CloudStorageService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val result = cloudStorageService.syncPendingDocuments()
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
