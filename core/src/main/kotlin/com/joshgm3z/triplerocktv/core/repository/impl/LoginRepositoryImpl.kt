package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import com.joshgm3z.triplerocktv.core.repository.retrofit.XtreamService
import com.joshgm3z.triplerocktv.core.repository.room.AppDatabase
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

private const val collection_logged_users = "logged_in_users"

class LoginRepositoryImpl @Inject constructor(
    private val localDataStore: LocalDatastore,
    private val appDatabase: AppDatabase,
    private val firestoreHelper: FirestoreHelper,
    private val firebaseLogger: FirebaseLogger,
) : LoginRepository {
    override suspend fun tryLogin(
        webUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // 1. Ensure URL ends with a slash for Retrofit
            val baseUrl = if (webUrl.endsWith("/")) webUrl else "$webUrl/"

            // 2. Build dynamic Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(XtreamService::class.java)

            // 3. Make the call
            val response = service.validateLogin(username, password)

            if (response.isSuccessful) {
                val body = response.body()
                // Xtream API returns auth = 1 on success
                if (body?.user_info?.auth == 1) {
                    val sessionId = firestoreHelper.createDocumentIdInCollection(
                        collection_logged_users,
                        mapOf(
                            "username" to username,
                            "timestamp" to System.currentTimeMillis(),
                            "status" to "logged in"
                        )
                    )
                    localDataStore.storeCredentials(
                        body,
                        webUrl,
                        password,
                        sessionId
                    )
                    delay(1000)
                    onSuccess()
                } else {
                    onError("Invalid username or password")
                }
            } else {
                onError("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            delay(1000)
            onError("Connection failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override suspend fun addIfNotExist() {
        localDataStore.getUserInfo()?.let {
            if (firestoreHelper.documentExists(
                    collection_logged_users,
                    it.sessionId
                )
            ) return

            firestoreHelper.addDocumentWithId(
                collection_logged_users,
                it.sessionId,
                mapOf(
                    "username" to it.username,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "logged in"
                )
            )
        }
    }

    override suspend fun tryLogout(onLogoutComplete: () -> Unit) {
        localDataStore.getUserInfo()?.let {
            firebaseLogger.logUserLogout(it.username)
            firestoreHelper.addDocumentWithId(
                collection_logged_users,
                it.sessionId,
                mapOf(
                    "username" to it.username,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "logged out"
                )
            )
        }
        localDataStore.clearAllData()
        appDatabase.clearAllTables()

        delay(1000)
        onLogoutComplete()
    }
}