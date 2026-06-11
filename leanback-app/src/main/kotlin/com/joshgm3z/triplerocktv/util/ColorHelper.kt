@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.joshgm3z.triplerocktv.util

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.joshgm3z.triplerocktv.R

@ColorInt
fun Context.getBackgroundColor(
): Int = getColorFromAttr(R.attr.colorBackground)

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int
): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrColor, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

@ColorInt
fun Context.getColorStateListFromAttr(
    @AttrRes attrColor: Int
): ColorStateList {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrColor, typedValue, true)
    return ContextCompat.getColorStateList(this, typedValue.resourceId)
}