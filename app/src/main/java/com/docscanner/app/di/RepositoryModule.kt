package com.docscanner.app.di

import com.docscanner.app.data.repository.AuthRepositoryImpl
import com.docscanner.app.data.repository.DocumentRepositoryImpl
import com.docscanner.app.data.repository.SettingsRepositoryImpl
import com.docscanner.app.data.repository.SyncRepositoryImpl
import com.docscanner.app.domain.repository.AuthRepository
import com.docscanner.app.domain.repository.DocumentRepository
import com.docscanner.app.domain.repository.SettingsRepository
import com.docscanner.app.domain.repository.SyncRepository
import com.docscanner.app.domain.repository.FolderRepository
import com.docscanner.app.data.repository.FolderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDocumentRepository(
        documentRepositoryImpl: DocumentRepositoryImpl
    ): DocumentRepository

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    abstract fun bindFolderRepository(
        folderRepositoryImpl: FolderRepositoryImpl
    ): FolderRepository
}
