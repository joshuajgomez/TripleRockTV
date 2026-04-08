package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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

}