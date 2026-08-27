package com.docscanner.app.data.repository

import com.docscanner.app.data.local.dao.DocumentDao
import com.docscanner.app.data.local.dao.FolderDao
import com.docscanner.app.data.local.dao.PageDao
import com.docscanner.app.data.mapper.toDomain
import com.docscanner.app.data.mapper.toEntity
import com.docscanner.app.domain.model.Document
import com.docscanner.app.domain.model.Page
import com.docscanner.app.domain.model.SyncStatus
import com.docscanner.app.domain.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val pageDao: PageDao
) : DocumentRepository {

    override fun getAllDocuments(): Flow<List<Document>> {
        return documentDao.getAllDocuments().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getDocumentById(id: String): Flow<Document?> {
        return documentDao.getDocumentById(id).map { it?.toDomain() }
    }

    override fun getDocumentsByFolder(folderId: String): Flow<List<Document>> {
        return documentDao.getDocumentsByFolder(folderId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchDocuments(query: String): Flow<List<Document>> {
        return documentDao.searchDocuments(query).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTrashedDocuments(): Flow<List<Document>> {
        return documentDao.getTrashedDocuments().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createDocument(
        title: String,
        pageImagePaths: List<String>,
        pdfPath: String?
    ): Document = withContext(Dispatchers.IO) {
        val docId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val doc = Document(
            id = docId,
            title = title,
            folderId = null,
            pageCount = pageImagePaths.size,
            thumbnailPath = pageImagePaths.firstOrNull() ?: "",
            ocrText = null,
            cloudPdfUrl = null,
            syncStatus = SyncStatus.LOCAL_ONLY,
            isEncrypted = false,
            isTrashed = false,
            trashedAt = null,
            createdAt = now,
            updatedAt = now
        )

        documentDao.upsert(doc.toEntity())

        pageImagePaths.forEachIndexed { index, path ->
            val page = Page(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                pageNumber = index + 1,
                originalImagePath = path,
                processedImagePath = path,
                thumbnailPath = path,
                width = 0,
                height = 0,
                rotation = 0,
                filter = com.docscanner.app.domain.model.FilterType.ORIGINAL,
                brightness = 0f,
                contrast = 0f,
                ocrText = null,
                ocrConfidence = null,
                createdAt = now
            )
            pageDao.insert(page.toEntity())
        }

        doc
    }

    override suspend fun updateDocument(document: Document) = withContext(Dispatchers.IO) {
        documentDao.upsert(document.toEntity())
    }

    override suspend fun renameDocument(docId: String, newTitle: String) = withContext(Dispatchers.IO) {
        documentDao.updateTitle(docId, newTitle)
    }

    override suspend fun moveToFolder(docId: String, folderId: String?) = withContext(Dispatchers.IO) {
        documentDao.updateFolder(docId, folderId)
    }

    override suspend fun moveToTrash(docId: String) = withContext(Dispatchers.IO) {
        documentDao.moveToTrash(docId, System.currentTimeMillis())
    }

    override suspend fun restoreFromTrash(docId: String) = withContext(Dispatchers.IO) {
        documentDao.restoreFromTrash(docId)
    }

    override suspend fun permanentlyDelete(docId: String) = withContext(Dispatchers.IO) {
        documentDao.delete(docId)
        pageDao.deleteByDocument(docId)
    }

    override suspend fun purgeOldTrash() = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000 // 30 days
        documentDao.purgeOldTrash(cutoff)
    }

    override suspend fun mergeDocuments(docIds: List<String>, newTitle: String): Document = withContext(Dispatchers.IO) {
        val docId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        var pageCount = 0

        val doc = Document(
            id = docId,
            title = newTitle,
            folderId = null,
            pageCount = 0,
            thumbnailPath = "",
            ocrText = null,
            cloudPdfUrl = null,
            syncStatus = SyncStatus.LOCAL_ONLY,
            isEncrypted = false,
            isTrashed = false,
            trashedAt = null,
            createdAt = now,
            updatedAt = now
        )

        documentDao.upsert(doc.toEntity())

        var firstThumb: String? = null
        for (sourceId in docIds) {
            val pages = pageDao.getPagesForDocumentSync(sourceId).map { it.toDomain() }
            for (page in pages) {
                pageCount++
                if (firstThumb == null) firstThumb = page.thumbnailPath
                val newPage = page.copy(
                    id = UUID.randomUUID().toString(),
                    documentId = docId,
                    pageNumber = pageCount,
                    createdAt = now
                )
                pageDao.insert(newPage.toEntity())
            }
        }

        val finalDoc = doc.copy(pageCount = pageCount, thumbnailPath = firstThumb ?: "")
        documentDao.upsert(finalDoc.toEntity())
        finalDoc
    }

    override suspend fun splitDocument(docId: String, splitAtPage: Int): Pair<Document, Document> = withContext(Dispatchers.IO) {
        val originalDoc = documentDao.getDocumentByIdSync(docId)?.toDomain()
            ?: throw IllegalArgumentException("Document not found")
        val pages = pageDao.getPagesForDocumentSync(docId).map { it.toDomain() }.sortedBy { it.pageNumber }

        val doc1Title = "${originalDoc.title} (1)"
        val doc2Title = "${originalDoc.title} (2)"

        val doc1 = createDocument(doc1Title, pages.take(splitAtPage).map { it.originalImagePath }, null)
        val doc2 = createDocument(doc2Title, pages.drop(splitAtPage).map { it.originalImagePath }, null)

        documentDao.updateFolder(doc1.id, originalDoc.folderId)
        documentDao.updateFolder(doc2.id, originalDoc.folderId)

        documentDao.delete(docId)
        Pair(documentDao.getDocumentByIdSync(doc1.id)!!.toDomain(), documentDao.getDocumentByIdSync(doc2.id)!!.toDomain())
    }

    override fun getPages(documentId: String): Flow<List<Page>> {
        return pageDao.getPagesByDocument(documentId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun updatePage(page: Page) = withContext(Dispatchers.IO) {
        pageDao.update(page.toEntity())
    }

    override suspend fun deletePage(pageId: String) = withContext(Dispatchers.IO) {
        pageDao.delete(pageId)
    }

    override suspend fun duplicatePage(pageId: String) = withContext(Dispatchers.IO) {
        val pageEntity = pageDao.getPageById(pageId) ?: return@withContext
        val newPage = pageEntity.copy(
            id = UUID.randomUUID().toString(),
            pageNumber = pageEntity.pageNumber + 1,
            createdAt = System.currentTimeMillis()
        )
        pageDao.insert(newPage)
    }

    override suspend fun reorderPages(documentId: String, pageIds: List<String>) = withContext(Dispatchers.IO) {
        pageIds.forEachIndexed { index, pageId ->
            pageDao.updatePageNumber(pageId, index + 1)
        }
    }

    override suspend fun addPages(documentId: String, pageImagePaths: List<String>) = withContext(Dispatchers.IO) {
        val currentCount = pageDao.getPageCount(documentId)
        val now = System.currentTimeMillis()
        val pages = pageImagePaths.mapIndexed { index, path ->
            Page(
                id = UUID.randomUUID().toString(),
                documentId = documentId,
                pageNumber = currentCount + index + 1,
                originalImagePath = path,
                processedImagePath = path,
                thumbnailPath = path,
                width = 0,
                height = 0,
                rotation = 0,
                filter = com.docscanner.app.domain.model.FilterType.ORIGINAL,
                brightness = 0f,
                contrast = 0f,
                ocrText = null,
                ocrConfidence = null,
                createdAt = now
            ).toEntity()
        }
        pageDao.insertAll(pages)
        val doc = documentDao.getDocumentByIdSync(documentId)
        if (doc != null) {
            val updatedDoc = doc.copy(pageCount = currentCount + pages.size)
            documentDao.upsert(updatedDoc)
        }
    }
}
