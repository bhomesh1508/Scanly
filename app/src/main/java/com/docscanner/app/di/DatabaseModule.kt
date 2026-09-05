package com.docscanner.app.di

import com.docscanner.app.data.local.db.AppDatabase
import com.docscanner.app.data.local.dao.CloudDocumentDao
import com.docscanner.app.data.local.dao.DocumentDao
import com.docscanner.app.data.local.dao.FolderDao
import com.docscanner.app.data.local.dao.PageDao
import com.docscanner.app.data.local.dao.SyncQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDocumentDao(db: AppDatabase): DocumentDao = db.documentDao()

    @Provides
    fun providePageDao(db: AppDatabase): PageDao = db.pageDao()

    @Provides
    fun provideFolderDao(db: AppDatabase): FolderDao = db.folderDao()

    @Provides
    fun provideSyncQueueDao(db: AppDatabase): SyncQueueDao = db.syncQueueDao()

    @Provides
    fun provideCloudDocumentDao(db: AppDatabase): CloudDocumentDao = db.cloudDocumentDao()
}
