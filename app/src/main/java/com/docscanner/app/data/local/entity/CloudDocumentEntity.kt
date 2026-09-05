package com.docscanner.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity caching remote cloud document metadata locally.
 */
@Entity(
    tableName = "cloud_documents",
    indices = [
        Index("localDocumentId"),
        Index("userId"),
        Index("uploadDate")
    ]
)
data class CloudDocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val localDocumentId: String?,
    val title: String,
    val fileType: String,
    val pageCount: Int,
    val fileSize: Long,
    val thumbnailUrl: String?,
    val cloudFileUrl: String?,
    val uploadDate: Long,
    val syncStatus: String
)
