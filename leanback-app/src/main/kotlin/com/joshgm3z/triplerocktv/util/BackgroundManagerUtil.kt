package com.joshgm3z.triplerocktv.util

import android.app.Activity
import android.graphics.Bitmap
import androidx.leanback.app.BackgroundManager
import com.joshgm3z.triplerocktv.core.util.Logger

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

fun callerName(): String {
    val element = Thread.currentThread().stackTrace[4]
    var className = element.className
    className = className.substring(className.lastIndexOf(".") + 1)
    val methodName = element.methodName
    return "$className:$methodName> "
}
