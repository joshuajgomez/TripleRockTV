package com.joshgm3z.triplerocktv.util

import android.app.Activity
import android.graphics.Bitmap
import androidx.leanback.app.BackgroundManager
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.callerName

fun Activity.setBackground(
    bitmap: Bitmap?,
) = BackgroundManager.getInstance(this).let {
    Logger.debug("bitmap ${if (bitmap == null) "null" else ""}(called by ${callerName()})")
    if (!it.isAttached) it.attach(window)
    it.setBitmap(bitmap)
}

fun Activity.setBackground(
    color: Int,
) = BackgroundManager.getInstance(this).let {
    Logger.debug("color (called by ${callerName()})")
    if (!it.isAttached) it.attach(window)
    it.color = color
}
