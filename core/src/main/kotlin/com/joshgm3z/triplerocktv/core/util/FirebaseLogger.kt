package com.joshgm3z.triplerocktv.core.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

enum class ScreenName(val value: String) {
    Splash("splash"),
    Login("login"),
    MediaUpdate("media_update"),
    Error("error"),
    Browse("browse"),
    Home("home"),
    Settings("settings"),
    Catalogue("catalogue"),
    VideoOnDemandDetails("video_on_demand_details"),
    SeriesDetails("series_details"),
    SeriesEpisodes("series_episodes"),
    Player("player"),
}

class FirebaseLogger
@Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logCustomEvent(eventName: String, params: Map<String, String>) {
        // Firebase analytics disabled for core module due to missing google-services.json
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logGlideError(uri: String) {
        logCustomEvent(
            "glide_error",
            mapOf("glide_error_uri" to uri)
        )
    }

    fun logUserLogin(username: String) {
        logCustomEvent(
            FirebaseAnalytics.Event.LOGIN,
            mapOf("login_username" to username)
        )
    }

    fun logUserLogout(username: String) {
        logCustomEvent(
            "user_logout",
            mapOf("logout_username" to username)
        )
    }

    fun logUserLoginFail(username: String) {
        logCustomEvent(
            "user_login_fail",
            mapOf("login_failed_username" to username)
        )
    }

    fun logScreenView(
        screenName: ScreenName,
        params: Map<String, String> = emptyMap(),
    ) {
        logCustomEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            mapOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screenName.name,
                FirebaseAnalytics.Param.SCREEN_CLASS to callerName(),
            ).plus(params).apply {
                Logger.debug("logScreenView: $this")
            }
        )
    }
}