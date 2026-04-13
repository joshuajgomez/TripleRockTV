package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.CompanionLoginRepository
import com.joshgm3z.triplerocktv.core.repository.CompanionLoginState
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import com.joshgm3z.triplerocktv.core.util.Logger
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
        return firestoreHelper.createDocumentIdInCollection(
            COLLECTION_NAME,
            mapOf("status" to "idle")
        )
    }

    override suspend fun deleteLoginSessionId(sessionId: String) {
        firestoreHelper.deleteDocumentWithId(COLLECTION_NAME, sessionId)
    }

    override fun listenStatus(
        sessionId: String,
        onState: (CompanionLoginState) -> Unit,
    ) {
        firestoreHelper.listenToDataMap(COLLECTION_NAME, sessionId) {
            when (it["status"] as? String) {
                "idle" -> onState(CompanionLoginState.Idle)
                "verifying" -> onState(CompanionLoginState.Verifying)
                "success" -> {
                    scope.launch {
                        Logger.debug("success $COLLECTION_NAME/$sessionId: $it")
                        delay(1000)
                        if (listOf(
                                "username",
                                "expiry_date",
                                "password",
                                "port",
                                "server_url"
                            ).all { key ->
                                if (!it.containsKey(key)) return@all false

                                val value = it[key]

                                if (value is String)
                                    if (value.isEmpty()) return@all false

                                if (value is Long)
                                    if (value.toString().isEmpty()) return@all false

                                return@all true
                            }
                        ) {
                            localDatastore.storeCredentials(
                                username = it["username"] as String,
                                expiryDate = (it["expiry_date"] as Long).toString(),
                                password = it["password"] as String,
                                port = (it["port"] as Long).toString(),
                                webUrl = it["server_url"] as String,
                            )
                            onState(CompanionLoginState.Success)
                        } else {
                            Logger.error("Missing some keys in firestore data $COLLECTION_NAME/$sessionId: $it")
                            onState(CompanionLoginState.Error)
                            return@launch
                        }
                    }
                }

                "error" -> onState(CompanionLoginState.Error)
            }
        }
    }
}