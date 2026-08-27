package com.docscanner.app.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining cloud synchronization operations.
 */
interface SyncRepository {

    /**
     * Synchronizes a specific document to the cloud.
     */
    suspend fun syncDocument(docId: String): Result<Unit>

    /**
     * Synchronizes all local changes with the cloud.
     */
    suspend fun syncAll(): Result<Unit>

    /**
     * Retrieves the current cloud storage usage for the user.
     * Returns a pair of (used bytes, limit bytes).
     */
    fun getStorageUsage(): Flow<Pair<Long, Long>>

    /**
     * Schedules periodic background synchronization via WorkManager.
     */
    fun schedulePeriodicSync()

    /**
     * Triggers an immediate background synchronization via WorkManager.
     */
    fun triggerImmediateSync()
}
