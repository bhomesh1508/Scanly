package com.docscanner.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    indices = [
        Index("folderId"),
        Index("title"),
        Index("createdAt"),
        Index("isTrashed")
    ]
)
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val folderId: String?,
    val pageCount: Int,
    val thumbnailPath: String,
    val ocrText: String?,
    val isEncrypted: Boolean,
    val isTrashed: Boolean,
    val trashedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
