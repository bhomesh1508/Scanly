package com.docscanner.app.util

object Constants {
    const val DB_NAME = "docscanner_db"
    const val DATASTORE_NAME = "docscanner_settings"
    const val DOCUMENTS_DIR = "documents"
    const val THUMBNAILS_DIR = "thumbnails"
    const val TEMP_DIR = "temp"
    
    const val MAX_SCAN_PAGES = 50
    const val TRASH_RETENTION_DAYS = 30
    const val FREE_STORAGE_LIMIT_BYTES = 2L * 1024 * 1024 * 1024
    const val MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024
    const val THUMBNAIL_MAX_SIZE = 256
    
    const val SYNC_WORK_NAME = "DocScannerPeriodicSync"
    const val SYNC_INTERVAL_HOURS = 1L
    
    const val SYNC_CHANNEL_ID = "sync_channel"
    const val SCAN_CHANNEL_ID = "scan_channel"
    
    const val FIREBASE_USERS_COLLECTION = "users"
    const val FIREBASE_DOCUMENTS_COLLECTION = "documents"
    const val FIREBASE_STORAGE_DOCUMENTS = "documents"
}
