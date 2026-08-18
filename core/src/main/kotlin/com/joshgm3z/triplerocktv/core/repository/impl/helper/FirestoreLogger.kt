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
    private var sessionId: String? = null

    init {
        scope.launch {
            sessionId = datastore.getUserInfo()?.sessionId
        }
    }

    fun log(dataMap: Map<String, Any>) {
        if (isDevBuild) sessionId?.let {
            firestoreHelper.log(it, dataMap)
        }
    }
}