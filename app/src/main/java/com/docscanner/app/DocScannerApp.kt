package com.docscanner.app

import android.app.Application
import com.docscanner.app.service.notification.NotificationService
import com.docscanner.app.service.sync.CloudSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DocScannerApp : Application() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var cloudSyncManager: CloudSyncManager

    override fun onCreate() {
        super.onCreate()
        
        notificationService.createNotificationChannels()
        cloudSyncManager.schedulePeriodicSync()
    }
}
