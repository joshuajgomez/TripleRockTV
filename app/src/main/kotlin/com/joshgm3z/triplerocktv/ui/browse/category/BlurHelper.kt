package com.joshgm3z.triplerocktv.ui.browse.category

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation

fun updateBackgroundWithBlur(
    context: Context,
    imageUrl: String,
    setBitmap: (Bitmap) -> Unit
) {
    Glide.with(context)
        .asBitmap()
        .load(imageUrl)
        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))) // radius, sampling
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val canvas = android.graphics.Canvas(resource)
                val paint = android.graphics.Paint()
                // Set color to black with 50% alpha (128)
                paint.colorFilter = colorFilter
                canvas.drawBitmap(resource, 0f, 0f, paint)
                setBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

val colorFilter = android.graphics.PorterDuffColorFilter(
    android.graphics.Color.argb(
        220,
        0,
        0,
        0
    ), // 180 is the darkness (0-255). Increase for darker, decrease for lighter.
    android.graphics.PorterDuff.Mode.SRC_ATOP
)