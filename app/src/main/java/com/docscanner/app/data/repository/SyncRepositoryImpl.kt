package com.docscanner.app.data.repository

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.docscanner.app.data.remote.sync.DocumentSyncWorker
import com.docscanner.app.data.remote.sync.SyncManager
import com.docscanner.app.domain.repository.AuthRepository
import com.docscanner.app.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncManager: SyncManager,
    private val authRepository: AuthRepository,
    private val context: Application
) : SyncRepository {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun syncDocument(docId: String): Result<Unit> {
        val user = authRepository.currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        return syncManager.syncDocument(docId, user.uid)
    }

    override suspend fun syncAll(): Result<Unit> {
        val user = authRepository.currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        return syncManager.syncAll(user.uid)
    }

    override fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<DocumentSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DocumentSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    override fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<DocumentSyncWorker>()
            .setConstraints(constraints)
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueue(syncRequest)
    }

    override fun getStorageUsage(): Flow<Pair<Long, Long>> {
        val user = authRepository.currentUser.value ?: return flow { emit(Pair(0L, 0L)) }
        return syncManager.getStorageUsage(user.uid)
    }
}
