package com.docscanner.app

import android.app.Application
import com.docscanner.app.service.notification.NotificationService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DocScannerApp : Application() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onCreate() {
        super.onCreate()
        
        notificationService.createNotificationChannels()
    }
}
