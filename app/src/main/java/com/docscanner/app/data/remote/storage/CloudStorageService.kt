package com.docscanner.app.data.remote.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for Firebase Cloud Storage operations.
 */
@Singleton
class CloudStorageService @Inject constructor(
    private val storage: FirebaseStorage
) {
    /**
     * Uploads a document (PDF) and reports progress.
     */
    suspend fun uploadDocument(
        userId: String, 
        docId: String, 
        filePath: String, 
        onProgress: (Float) -> Unit
    ): Result<String> {
        return try {
            val file = File(filePath)
            val uri = Uri.fromFile(file)
            val ref = storage.reference.child("users/$userId/documents/$docId/document.pdf")
            
            val uploadTask = ref.putFile(uri)
            
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                onProgress(progress.toFloat())
            }.await()
            
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads a document thumbnail image.
     */
    suspend fun uploadThumbnail(userId: String, docId: String, filePath: String): Result<String> {
        return try {
            val file = File(filePath)
            val uri = Uri.fromFile(file)
            val ref = storage.reference.child("users/$userId/documents/$docId/thumbnail.jpg")
            
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads a document (PDF) to the local destination.
     */
    suspend fun downloadDocument(userId: String, docId: String, destinationPath: String): Result<Unit> {
        return try {
            val file = File(destinationPath)
            val ref = storage.reference.child("users/$userId/documents/$docId/document.pdf")
            ref.getFile(file).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes all files associated with a document.
     */
    suspend fun deleteDocumentFiles(userId: String, docId: String): Result<Unit> {
        return try {
            val docRef = storage.reference.child("users/$userId/documents/$docId/document.pdf")
            val thumbRef = storage.reference.child("users/$userId/documents/$docId/thumbnail.jpg")
            
            try { docRef.delete().await() } catch (e: Exception) { /* ignore if not found */ }
            try { thumbRef.delete().await() } catch (e: Exception) { /* ignore if not found */ }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calculates total storage used by a user by listing all their files.
     */
    suspend fun getStorageUsage(userId: String): Long {
        return try {
            var totalBytes = 0L
            val rootRef = storage.reference.child("users/$userId")
            val result = rootRef.listAll().await()
            
            // This is a naive implementation; Firebase Storage doesn't have a simple 
            // "folder size" API, so listing prefix or keeping track in Firestore is better.
            // For this simulation, assuming flat prefixes if any or just listing documents.
            for (prefix in result.prefixes) {
                val subResult = prefix.listAll().await()
                for (item in subResult.items) {
                    totalBytes += item.metadata.await().sizeBytes
                }
            }
            for (item in result.items) {
                totalBytes += item.metadata.await().sizeBytes
            }
            totalBytes
        } catch (e: Exception) {
            0L
        }
    }
}
