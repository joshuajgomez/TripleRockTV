package com.joshgm3z.triplerocktv.core.repository.impl

import android.content.Context
import com.joshgm3z.triplerocktv.core.R
import com.joshgm3z.triplerocktv.core.repository.OnlineTyperRepository
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val COLLECTION = "online_typing_sessions"

class OnlineTyperRepositoryImpl
@Inject constructor(
    scope: CoroutineScope,
    private val firestoreHelper: FirestoreHelper,
    private val localDataStore: LocalDatastore,
    @param:ApplicationContext private val context: Context,
) : OnlineTyperRepository {

    private lateinit var sessionId: String

    init {
        scope.launch {
            val localSessionId = localDataStore.getUserInfo()?.sessionId
            sessionId = localSessionId ?: firestoreHelper.createDocumentIdInCollection(COLLECTION)
        }
    }

    override suspend fun newTypingSessionUrl(): String? =
        if (firestoreHelper.addDocumentWithId(COLLECTION, sessionId)) {
            val baseUrl = context.getString(R.string.online_typer_app_url)
            "$baseUrl?id=$sessionId"
        } else null

    override suspend fun deleteTypingSession() {
        firestoreHelper.deleteDocumentWithId(COLLECTION, sessionId)
    }

    override fun listenInput(sessionId: String, onInput: (String) -> Unit) {
        Logger.debug("sessionId = [${sessionId}]")
        firestoreHelper.listenToDataMap(COLLECTION, sessionId) {
            (it["input"] as? String)?.let { input -> onInput(input) }
        }
    }
}