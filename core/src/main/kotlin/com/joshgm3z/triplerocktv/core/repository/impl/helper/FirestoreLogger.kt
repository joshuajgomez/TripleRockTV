package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirestoreLogger
@Inject constructor(
    datastore: LocalDatastore,
    scope: CoroutineScope,
    private val firestoreHelper: FirestoreHelper
) {
    private lateinit var sessionId: String

    init {
        scope.launch {
            sessionId = datastore.getUserInfo()?.sessionId
                ?: throw Exception("Session ID not found")
        }
    }

    fun log(dataMap: Map<String, Any>) {
        if (isDevBuild) firestoreHelper.log(sessionId, dataMap)
    }
}