package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreHelper
@Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getDataMap(collectionPath: String, documentId: String): Map<String, Any>? {
        Logger.debug("collectionPath = [${collectionPath}], documentId = [${documentId}]")
        return try {
            db.collection(collectionPath).document(documentId)
                .get().await().data
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            null
        }
    }

    suspend fun createDocumentIdInCollection(
        collection: String,
        initialData: Map<String, Any> = mapOf("input" to "")
    ): String? {
        val document = db.collection(collection)
            .add(initialData)
            .await()
        return document.id
    }

    fun listenToDataMap(
        collection: String,
        documentId: String,
        onData: (Map<String, Any>) -> Unit
    ) {
        db.collection(collection)
            .document(documentId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    if (data != null) {
                        Logger.debug("data = [${data}]")
                        onData(data)
                    }
                }
            }
    }

    suspend fun deleteDocumentWithId(collection: String, sessionId: String): Boolean {
        return try {
            db.collection(collection)
                .document(sessionId)
                .delete()
                .await()
            Logger.debug("Deleted $collection/$sessionId")
            true
        } catch (e: Exception) {
            Logger.error("Failed to delete $collection/$sessionId: ${e.message}")
            false
        }
    }
}
