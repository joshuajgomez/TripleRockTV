package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AccessControlData(
    val globalAccessEnabled: Boolean = true,
    val globalAccessReason: String = "",
    val bannedUsers: Map<String, String> = emptyMap(),
    val forcedMinAppVersion: String = ""
)

class FirestoreHelper @Inject constructor() {

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

    fun getCollectionFlow(collectionPath: String): Flow<AccessControlData> =
        callbackFlow {
            Logger.debug("collectionPath = [$collectionPath]")
            val registration = db.collection(collectionPath)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Logger.error("Error listening to collection: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val data = snapshot.documents.mapNotNull { it.toObject(AccessControlData::class.java) }
                        trySend(data)
                    }
                }

            awaitClose {
                Logger.debug("Closing listener for $collectionPath")
                registration.remove()
            }
        }

}