package com.joshgm3z.triplerocktv.core.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joshgm3z.triplerocktv.core.repository.retrofit.XtreamUserResponse
import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
        private val LAST_CONTENT_UPDATE = stringPreferencesKey("last_content_update")
        private val SERVER_PORT = stringPreferencesKey("server_port")
        private val EXPIRY_DATE = stringPreferencesKey("expiry_date")

        private val BLUR_SETTING = booleanPreferencesKey("blur_setting")
    }

    suspend fun storeCredentials(
        xtreamUserResponse: XtreamUserResponse,
        webUrl: String,
        password: String,
    ) = storeCredentials(
        xtreamUserResponse.user_info?.username ?: "",
        xtreamUserResponse.user_info?.exp_date ?: "",
        xtreamUserResponse.server_info?.port ?: "",
        webUrl,
        password
    )

    suspend fun storeCredentials(
        username: String,
        expiryDate: String,
        port: String,
        webUrl: String,
        password: String,
    ) = dataStore.edit { preferences ->
        preferences[USERNAME] = username
        preferences[EXPIRY_DATE] = expiryDate
        preferences[PASSWORD] = password
        preferences[SERVER_URL] = webUrl
        preferences[SERVER_PORT] = port
    }

    suspend fun getUserInfo() = dataStore.data.firstOrNull()?.let {
        UserInfo(
            username = it[USERNAME] ?: return null,
            password = it[PASSWORD] ?: return null,
            webUrl = it[SERVER_URL] ?: return null,
            expiryDate = it[EXPIRY_DATE] ?: return null,
            lastContentUpdate = it[LAST_CONTENT_UPDATE] ?: "",
        )
    }

    suspend fun setLastContentUpdate(date: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_CONTENT_UPDATE] = date.toString()
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }

    suspend fun setBlurSetting(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BLUR_SETTING] = enabled
        }
    }

    fun blurSettingFlow(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[BLUR_SETTING] ?: true }
}
