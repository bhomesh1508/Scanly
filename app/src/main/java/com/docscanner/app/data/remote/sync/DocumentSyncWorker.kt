package com.docscanner.app.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.docscanner.app.data.remote.auth.FirebaseAuthService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker to sync documents periodically.
 */
@HiltWorker
class DocumentSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val authService: FirebaseAuthService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val user = authService.getCurrentUser() ?: return Result.failure()
        
        return try {
            val result = syncManager.syncAll(user.uid)
            if (result.isSuccess) {
                Result.success()
            } else {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
