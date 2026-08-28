package com.docscanner.app.data.mapper

import com.docscanner.app.data.local.entity.DocumentEntity
import com.docscanner.app.data.local.entity.FolderEntity
import com.docscanner.app.data.local.entity.PageEntity
import com.docscanner.app.domain.model.Document
import com.docscanner.app.domain.model.FilterType
import com.docscanner.app.domain.model.Folder
import com.docscanner.app.domain.model.Page

fun DocumentEntity.toDomain(): Document {
    return Document(
        id = this.id,
        title = this.title,
        folderId = this.folderId,
        pageCount = this.pageCount,
        thumbnailPath = this.thumbnailPath,
        ocrText = this.ocrText,
        isEncrypted = this.isEncrypted,
        isTrashed = this.isTrashed,
        trashedAt = this.trashedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Document.toEntity(): DocumentEntity {
    return DocumentEntity(
        id = this.id,
        title = this.title,
        folderId = this.folderId,
        pageCount = this.pageCount,
        thumbnailPath = this.thumbnailPath,
        ocrText = this.ocrText,
        isEncrypted = this.isEncrypted,
        isTrashed = this.isTrashed,
        trashedAt = this.trashedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun PageEntity.toDomain(): Page {
    return Page(
        id = this.id,
        documentId = this.documentId,
        pageNumber = this.pageNumber,
        originalImagePath = this.originalImagePath,
        processedImagePath = this.processedImagePath,
        thumbnailPath = this.thumbnailPath,
        width = this.width,
        height = this.height,
        rotation = this.rotation,
        filter = try { FilterType.valueOf(this.filter) } catch (e: IllegalArgumentException) { FilterType.ORIGINAL },
        brightness = this.brightness,
        contrast = this.contrast,
        ocrText = this.ocrText,
        ocrConfidence = this.ocrConfidence,
        createdAt = this.createdAt
    )
}

fun Page.toEntity(): PageEntity {
    return PageEntity(
        id = this.id,
        documentId = this.documentId,
        pageNumber = this.pageNumber,
        originalImagePath = this.originalImagePath,
        processedImagePath = this.processedImagePath,
        thumbnailPath = this.thumbnailPath,
        width = this.width,
        height = this.height,
        rotation = this.rotation,
        filter = this.filter.name,
        brightness = this.brightness,
        contrast = this.contrast,
        ocrText = this.ocrText,
        ocrConfidence = this.ocrConfidence,
        createdAt = this.createdAt
    )
}

fun FolderEntity.toDomain(): Folder {
    return Folder(
        id = this.id,
        name = this.name,
        color = this.color,
        documentCount = this.documentCount,
        createdAt = this.createdAt
    )
}

fun Folder.toEntity(): FolderEntity {
    return FolderEntity(
        id = this.id,
        name = this.name,
        color = this.color,
        documentCount = this.documentCount,
        createdAt = this.createdAt
    )
}
