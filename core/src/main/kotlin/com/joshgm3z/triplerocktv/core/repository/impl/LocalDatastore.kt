package com.joshgm3z.triplerocktv.core.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joshgm3z.triplerocktv.core.repository.retrofit.XtreamUserResponse
import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocalDatastore
@Inject
constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val LAST_MEDIA_SYNC = stringPreferencesKey("last_media_sync")
        private val SERVER_PORT = stringPreferencesKey("server_port")
        private val SESSION_ID = stringPreferencesKey("session_id")
        private val EXPIRY_DATE = stringPreferencesKey("expiry_date")
    }

    suspend fun storeCredentials(
        xtreamUserResponse: XtreamUserResponse,
        webUrl: String,
        password: String,
        sessionId: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = xtreamUserResponse.user_info?.username ?: ""
            preferences[EXPIRY_DATE] = xtreamUserResponse.user_info?.exp_date ?: ""
            preferences[PASSWORD] = password
            preferences[SERVER_URL] = webUrl
            preferences[SERVER_PORT] = xtreamUserResponse.server_info?.port ?: ""
            preferences[SESSION_ID] = sessionId
        }
    }

    suspend fun getUserInfo() = dataStore.data.firstOrNull()?.let {
        UserInfo(
            username = it[USERNAME] ?: return null,
            password = it[PASSWORD] ?: return null,
            webUrl = it[SERVER_URL] ?: return null,
            expiryDate = it[EXPIRY_DATE] ?: return null,
            sessionId = it[SESSION_ID] ?: ""
        )
    }

    suspend fun notifyLastContentUpdate(date: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_MEDIA_SYNC] = date.toString()
        }
    }

    suspend fun getLastContentUpdate(): Long {
        val data = dataStore.data.firstOrNull()
        return data?.get(LAST_MEDIA_SYNC)?.toLongOrNull() ?: 0L
    }

    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }

}
