package com.joshgm3z.triplerocktv.util

import android.view.View
import com.joshgm3z.triplerocktv.BuildConfig

fun View.setVisible(visible: Boolean?) {
    visibility = if (visible == true) View.VISIBLE else View.GONE
}

fun String.orIfDebug(secretText: String) = if (BuildConfig.DEBUG) secretText else this