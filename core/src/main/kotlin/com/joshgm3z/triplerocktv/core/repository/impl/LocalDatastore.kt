package com.joshgm3z.triplerocktv.core.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joshgm3z.triplerocktv.core.repository.StreamType
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
        private val LAST_CONTENT_UPDATE_VOD = stringPreferencesKey("last_content_update_vod")
        private val TOTAL_COUNT_VOD = stringPreferencesKey("total_count_vod")
        private val TOTAL_COUNT_SERIES = stringPreferencesKey("total_count_series")
        private val TOTAL_COUNT_LIVE_TV = stringPreferencesKey("total_count_live_tv")
        private val LAST_CONTENT_UPDATE_SERIES = stringPreferencesKey("last_content_update_series")
        private val LAST_CONTENT_UPDATE_LIVE_TV =
            stringPreferencesKey("last_content_update_live_tv")
        private val SERVER_PORT = stringPreferencesKey("server_port")
        private val SESSION_ID = stringPreferencesKey("session_id")
        private val EXPIRY_DATE = stringPreferencesKey("expiry_date")

        private val BLUR_SETTING = booleanPreferencesKey("blur_setting")
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
            lastContentUpdate = it[LAST_CONTENT_UPDATE] ?: "",
            sessionId = it[SESSION_ID] ?: ""
        )
    }

    suspend fun setLastContentUpdate(date: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_CONTENT_UPDATE] = date.toString()
        }
    }

    private fun lastContentPref(type: StreamType) = when (type) {
        StreamType.VideoOnDemand -> LAST_CONTENT_UPDATE_VOD
        StreamType.Series -> LAST_CONTENT_UPDATE_SERIES
        StreamType.LiveTV -> LAST_CONTENT_UPDATE_LIVE_TV
    }

    private fun totalCountPref(type: StreamType) = when (type) {
        StreamType.VideoOnDemand -> TOTAL_COUNT_VOD
        StreamType.Series -> TOTAL_COUNT_SERIES
        StreamType.LiveTV -> TOTAL_COUNT_LIVE_TV
    }

    suspend fun setLastContentUpdate(
        type: StreamType,
        date: Long,
        countText: String,
    ) {
        dataStore.edit { preferences ->
            preferences[lastContentPref(type)] = date.toString()
            preferences[totalCountPref(type)] = countText
        }
    }

    suspend fun getTotalCount(type: StreamType): String {
        val data = dataStore.data.firstOrNull()
        return data?.get(totalCountPref(type)) ?: ""
    }

    suspend fun getLastContentUpdate(type: StreamType): Long {
        val data = dataStore.data.firstOrNull()
        return data?.get(lastContentPref(type))?.toLongOrNull() ?: 0L
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
