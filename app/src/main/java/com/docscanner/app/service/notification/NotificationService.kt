package com.docscanner.app.service.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(private val context: Application) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val syncChannel = NotificationChannel(
                "sync_channel",
                "Cloud Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for cloud sync progress and completion"
            }

            val scanChannel = NotificationChannel(
                "scan_channel",
                "Scan Complete",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when document scanning is completed"
            }

            notificationManager.createNotificationChannels(listOf(syncChannel, scanChannel))
        }
    }

    fun showSyncProgressNotification(progress: Int) {
        val builder = NotificationCompat.Builder(context, "sync_channel")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Syncing Documents")
            .setContentText("Sync in progress...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, progress, false)
            .setOngoing(true)

        notificationManager.notify(SYNC_NOTIFICATION_ID, builder.build())
    }

    fun showSyncCompleteNotification(docCount: Int) {
        val builder = NotificationCompat.Builder(context, "sync_channel")
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("Sync Complete")
            .setContentText("Successfully synced $docCount documents")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)

        notificationManager.notify(SYNC_NOTIFICATION_ID, builder.build())
    }

    fun showScanCompleteNotification(documentTitle: String) {
        val builder = NotificationCompat.Builder(context, "scan_channel")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("Scan Complete")
            .setContentText("Successfully scanned: $documentTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(SCAN_NOTIFICATION_ID, builder.build())
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    companion object {
        const val SYNC_NOTIFICATION_ID = 101
        const val SCAN_NOTIFICATION_ID = 102
    }
}
