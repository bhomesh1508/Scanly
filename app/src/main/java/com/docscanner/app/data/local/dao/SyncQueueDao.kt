package com.docscanner.app.data.local.dao

import androidx.room.*
import com.docscanner.app.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingTasks(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun observePendingTasks(): Flow<List<SyncQueueEntity>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun observePendingCount(): Flow<Int>

    @Upsert
    suspend fun upsert(task: SyncQueueEntity)

    @Upsert
    suspend fun upsertAll(tasks: List<SyncQueueEntity>)

    @Query("UPDATE sync_queue SET status = :status, errorMessage = :error, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, error: String?, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun incrementRetry(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM sync_queue WHERE documentId = :documentId")
    suspend fun deleteByDocumentId(documentId: String)

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}
