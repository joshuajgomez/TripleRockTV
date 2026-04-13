package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.CompanionLoginRepository
import com.joshgm3z.triplerocktv.core.repository.CompanionLoginState
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val COLLECTION_NAME = "companion_login_sessions"

class CompanionLoginRepositoryImpl
@Inject constructor(
    private val firestoreHelper: FirestoreHelper,
    private val localDatastore: LocalDatastore,
    private val scope: CoroutineScope,
) : CompanionLoginRepository {
    override suspend fun newLoginSessionId(): String? {
        return firestoreHelper.createDocumentIdInCollection(COLLECTION_NAME)
    }

    override suspend fun deleteLoginSessionId(sessionId: String) {
        firestoreHelper.deleteDocumentWithId(COLLECTION_NAME, sessionId)
    }

    override fun listenStatus(
        sessionId: String,
        onState: (CompanionLoginState) -> Unit,
    ) {
        firestoreHelper.listenToDataMap(COLLECTION_NAME, sessionId) {
            when (it["status"] as String) {
                "idle" -> onState(CompanionLoginState.Idle)
                "verifying" -> onState(CompanionLoginState.Verifying)
                "success" -> {
                    scope.launch {
                        localDatastore.storeCredentials(
                            username = it["username"] as String,
                            expiryDate = it["expiry_date"] as String,
                            password = it["password"] as String,
                            port = it["port"] as String,
                            webUrl = it["web_url"] as String,
                        )
                        delay(1000)
                        onState(CompanionLoginState.Success)
                    }
                }

                "error" -> onState(CompanionLoginState.Error)
            }
        }
    }
}