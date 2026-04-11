package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.OnlineTyperRepository
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val COLLECTION = "online_typing_sessions"

class OnlineTyperRepositoryImpl
@Inject constructor(
    private val firestoreHelper: FirestoreHelper
) : OnlineTyperRepository {

    override suspend fun newTypingSessionId(): String {
//        return firestoreHelper.createDocumentIdInCollection(COLLECTION)
        return "8YbHPUozYpVk2StV0smE"
    }

    override fun listenInput(sessionId: String, onInput: (String) -> Unit) {
        Logger.debug("sessionId = [${sessionId}]")
        firestoreHelper.listenToDataMap(COLLECTION, sessionId) {
            onInput(it["input"] as String)
        }
    }
}