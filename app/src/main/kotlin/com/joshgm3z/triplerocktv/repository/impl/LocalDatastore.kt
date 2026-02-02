package com.joshgm3z.triplerocktv.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joshgm3z.triplerocktv.repository.retrofit.XtreamUserResponse
import com.joshgm3z.triplerocktv.ui.login.UserInfo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LocalDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val LAST_CONTENT_UPDATE = stringPreferencesKey("last_content_update")
        private val SERVER_PORT = stringPreferencesKey("server_port")
        private val EXPIRY_DATE = stringPreferencesKey("expiry_date")
    }

    suspend fun storeCredentials(
        xtreamUserResponse: XtreamUserResponse,
        webUrl: String,
        password: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = xtreamUserResponse.user_info?.username ?: ""
            preferences[EXPIRY_DATE] = xtreamUserResponse.user_info?.exp_date ?: ""
            preferences[PASSWORD] = password
            preferences[SERVER_URL] = webUrl
            preferences[SERVER_PORT] = xtreamUserResponse.server_info?.port ?: ""
        }
    }

    suspend fun getServerUrl(): String? {
        return dataStore.data.map { it[SERVER_URL] }.firstOrNull()
    }

    fun getLoginCredentials(
        onFetch: (UserInfo) -> Unit
    ) = runBlocking {
        dataStore.data.firstOrNull()?.let {
            onFetch(
                UserInfo(
                    username = it[USERNAME] ?: "",
                    password = it[PASSWORD] ?: "",
                    webUrl = it[SERVER_URL] ?: "",
                    expiryDate = it[EXPIRY_DATE] ?: "",
                    lastContentUpdate = it[LAST_CONTENT_UPDATE] ?: "",
                )
            )
        }
    }

    suspend fun setLastContentUpdate(date: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_CONTENT_UPDATE] = date.toString()
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }
}
