package com.docscanner.app.domain.model

/**
 * Represents a document in the application.
 *
 * @property id Unique identifier for the document.
 * @property title Title of the document.
 * @property folderId ID of the folder containing this document, if any.
 * @property pageCount Number of pages in the document.
 * @property thumbnailPath Path to the thumbnail image of the document.
 * @property ocrText Extracted text from all pages in the document.
 * @property cloudPdfUrl URL to the uploaded PDF in cloud storage, if any.
 * @property syncStatus Current sync status of the document.
 * @property isEncrypted Whether the document is encrypted locally/cloud.
 * @property isTrashed Whether the document is moved to trash.
 * @property trashedAt Timestamp when the document was moved to trash.
 * @property createdAt Timestamp when the document was created.
 * @property updatedAt Timestamp when the document was last updated.
 */
data class Document(
    val id: String,
    val title: String,
    val folderId: String? = null,
    val pageCount: Int,
    val thumbnailPath: String,
    val ocrText: String? = null,
    val cloudPdfUrl: String? = null,
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val isEncrypted: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Enumeration representing the sync status of a document.
 */
enum class SyncStatus {
    LOCAL_ONLY, SYNCING, SYNCED, SYNC_FAILED
}
