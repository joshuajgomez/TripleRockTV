package com.joshgm3z.triplerocktv.core.util

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class FirebaseConfig
@Inject constructor() {
    val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (isDevBuild) 0 else 3600 // 1 hour fetch interval
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Logger.debug("Config params updated: $updated")
                } else {
                    Logger.warn("Error fetching config")
                }
            }
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = remoteConfig.getString(key)
        Logger.debug("remoteConfig.getString(\"$key\") = [$json]")
        return try {
            if (json.isEmpty()) null else Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            Logger.warn("Error parsing JSON for key $key: ${e.message}")
            null
        }
    }

    fun getString(key: String): String? {
        val string = remoteConfig.getString(key)
        return string.trim().ifEmpty { null }
    }
}