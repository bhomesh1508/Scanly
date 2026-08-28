package com.docscanner.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.docscanner.app.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE isTrashed = 0 ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getDocumentById(id: String): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentByIdSync(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE folderId = :folderId AND isTrashed = 0 ORDER BY updatedAt DESC")
    fun getDocumentsByFolder(folderId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isTrashed = 0 AND (title LIKE '%' || :query || '%' OR ocrText LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isTrashed = 1 ORDER BY trashedAt DESC")
    fun getTrashedDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT COUNT(*) FROM documents WHERE isTrashed = 0")
    fun getDocumentCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(document: DocumentEntity)

    @Query("UPDATE documents SET title = :title WHERE id = :docId")
    suspend fun updateTitle(docId: String, title: String)

    @Query("UPDATE documents SET folderId = :folderId WHERE id = :docId")
    suspend fun updateFolder(docId: String, folderId: String?)

    @Query("UPDATE documents SET isTrashed = 1, trashedAt = :trashedAt WHERE id = :docId")
    suspend fun moveToTrash(docId: String, trashedAt: Long)

    @Query("UPDATE documents SET isTrashed = 0, trashedAt = NULL WHERE id = :docId")
    suspend fun restoreFromTrash(docId: String)

    @Query("DELETE FROM documents WHERE id = :docId")
    suspend fun delete(docId: String)

    @Query("DELETE FROM documents WHERE isTrashed = 1 AND trashedAt < :cutoff")
    suspend fun purgeOldTrash(cutoff: Long)
}
