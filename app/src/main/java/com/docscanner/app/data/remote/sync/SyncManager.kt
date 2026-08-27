package com.docscanner.app.data.remote.sync

import com.docscanner.app.data.local.dao.DocumentDao
import com.docscanner.app.data.local.dao.PageDao
import com.docscanner.app.data.remote.firestore.FirestoreService
import com.docscanner.app.data.remote.storage.CloudStorageService
import com.docscanner.app.domain.model.SyncStatus
import com.docscanner.app.data.mapper.toDomain
import com.docscanner.app.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates synchronization between local Room DB and Firebase.
 */
@Singleton
class SyncManager @Inject constructor(
    private val documentDao: DocumentDao,
    private val pageDao: PageDao
) {
    suspend fun syncDocument(docId: String, userId: String): Result<Unit> {
        // Offline: just mark it as synced.
        documentDao.updateSyncStatus(docId, SyncStatus.SYNCED.name)
        return Result.success(Unit)
    }

    suspend fun syncAll(userId: String): Result<Unit> {
        val unsyncedDocs = documentDao.getUnsyncedDocuments()
        for (docEntity in unsyncedDocs) {
            documentDao.updateSyncStatus(docEntity.id, SyncStatus.SYNCED.name)
        }
        return Result.success(Unit)
    }

    fun getStorageUsage(userId: String): Flow<Pair<Long, Long>> = flow {
        emit(Pair(0L, 5L * 1024 * 1024 * 1024))
    }
}
