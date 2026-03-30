package com.joshgm3z.triplerocktv.util

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class FirebaseLogger {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logCustomEvent(eventName: String, params: Map<String, String>) {
        // Firebase analytics disabled for core module due to missing google-services.json
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    companion object {
        private var INSTANCE: FirebaseLogger? = null

        private fun getInstance(): FirebaseLogger {
            if (INSTANCE == null) {
                INSTANCE = FirebaseLogger()
            }
            return INSTANCE!!
        }

        fun logGlideError(uri: String) {
            getInstance().logCustomEvent("glide_error", mapOf("uri" to uri))
        }
    }
}