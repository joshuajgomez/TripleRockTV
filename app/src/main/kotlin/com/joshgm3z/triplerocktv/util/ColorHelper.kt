package com.joshgm3z.triplerocktv.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.joshgm3z.triplerocktv.R

@ColorInt
fun Context.getBackgroundColor(
): Int = getColorFromAttr(R.attr.backgroundColor)

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int
): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrColor, typedValue, true)
    return typedValue.data
}