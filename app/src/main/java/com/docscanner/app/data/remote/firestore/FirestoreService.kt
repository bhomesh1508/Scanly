package com.docscanner.app.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for Firestore database operations.
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Saves a document's metadata to Firestore.
     */
    suspend fun saveDocument(userId: String, docMetadata: Map<String, Any>) {
        val docId = docMetadata["id"] as? String ?: return
        firestore.collection("users").document(userId)
            .collection("documents").document(docId)
            .set(docMetadata, SetOptions.merge())
            .await()
    }

    /**
     * Gets a real-time flow of a user's documents.
     */
    fun getDocuments(userId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("documents")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val docs = snapshot.documents.mapNotNull { it.data }
                    trySend(docs)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Deletes a document from Firestore.
     */
    suspend fun deleteDocument(userId: String, docId: String) {
        firestore.collection("users").document(userId)
            .collection("documents").document(docId)
            .delete()
            .await()
    }

    /**
     * Saves a folder to Firestore.
     */
    suspend fun saveFolder(userId: String, folder: Map<String, Any>) {
        val folderId = folder["id"] as? String ?: return
        firestore.collection("users").document(userId)
            .collection("folders").document(folderId)
            .set(folder, SetOptions.merge())
            .await()
    }

    /**
     * Gets a real-time flow of a user's folders.
     */
    fun getFolders(userId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("folders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val folders = snapshot.documents.mapNotNull { it.data }
                    trySend(folders)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Saves user settings to Firestore.
     */
    suspend fun saveSettings(userId: String, settings: Map<String, Any>) {
        firestore.collection("users").document(userId)
            .collection("settings").document("preferences")
            .set(settings, SetOptions.merge())
            .await()
    }

    /**
     * Gets a real-time flow of user settings.
     */
    fun getSettings(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("settings").document("preferences")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.data)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Updates the user's storage usage tracking in their profile.
     */
    suspend fun updateStorageUsage(userId: String, bytesUsed: Long) {
        val data = mapOf("cloudStorageUsedBytes" to bytesUsed)
        firestore.collection("users").document(userId)
            .set(data, SetOptions.merge())
            .await()
    }

    /**
     * Gets a real-time flow of the user profile.
     */
    fun getUserProfile(userId: String): Flow<Map<String, Any>?> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.data)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Initializes a user profile if one doesn't exist.
     */
    suspend fun initUserProfile(userId: String, email: String, displayName: String?) {
        val profileRef = firestore.collection("users").document(userId)
        val snapshot = profileRef.get().await()
        if (!snapshot.exists()) {
            val initialData = mapOf(
                "uid" to userId,
                "email" to email,
                "displayName" to (displayName ?: ""),
                "cloudStorageUsedBytes" to 0L,
                "cloudStorageLimitBytes" to 5L * 1024 * 1024 * 1024, // 5GB default
                "createdAt" to System.currentTimeMillis()
            )
            profileRef.set(initialData).await()
        }
    }
}
