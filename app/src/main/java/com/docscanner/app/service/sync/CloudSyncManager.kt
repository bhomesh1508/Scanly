package com.docscanner.app.service.sync

import android.content.Context
import androidx.work.*
import com.docscanner.app.domain.model.UserSettings
import com.docscanner.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val workManager = WorkManager.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun schedulePeriodicSync() {
        scope.launch {
            val settings = settingsRepository.settings.first()
            if (!settings.cloudBackupEnabled || !settings.autoSyncEnabled) {
                workManager.cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
                return@launch
            }

            val networkType = if (settings.wifiOnlyUpload) {
                NetworkType.UNMETERED
            } else {
                NetworkType.CONNECTED
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(networkType)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<CloudSyncWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            workManager.enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncRequest
            )
        }
    }

    fun triggerImmediateSync() {
        scope.launch {
            val settings = settingsRepository.settings.first()
            val networkType = if (settings.wifiOnlyUpload) {
                NetworkType.UNMETERED
            } else {
                NetworkType.CONNECTED
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(networkType)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<CloudSyncWorker>()
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                ONE_TIME_SYNC_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }
    }

    companion object {
        private const val PERIODIC_SYNC_WORK_NAME = "scanly_periodic_cloud_sync"
        private const val ONE_TIME_SYNC_WORK_NAME = "scanly_immediate_cloud_sync"
    }
}
