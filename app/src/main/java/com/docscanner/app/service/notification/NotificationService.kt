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
            val scanChannel = NotificationChannel(
                "scan_channel",
                "Scan Complete",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when document scanning is completed"
            }

            notificationManager.createNotificationChannel(scanChannel)
        }
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
        const val SCAN_NOTIFICATION_ID = 102
    }
}
