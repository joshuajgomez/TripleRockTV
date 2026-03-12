package com.joshgm3z.triplerocktv.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlideUtil
@Inject constructor(
    scope: CoroutineScope,
    private val localDatastore: LocalDatastore
) {
    private var serverUrl = ""

    init {
        scope.launch {
            serverUrl = localDatastore.getUserInfo()?.webUrl ?: ""
        }
    }

    fun loadImage(url: String?, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url.alternateUri(serverUrl))
            .centerCrop()
            .into(imageView)
    }

    fun loadBitmap(
        context: Context,
        uri: String?,
        onBitmapReady: (Bitmap) -> Unit,
    ) {
        Glide.with(context)
            .asBitmap()
            .load(uri.alternateUri(serverUrl))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    onBitmapReady(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}

fun String?.alternateUri(serverUrl: String): String? = when {
    this == null -> null
    else -> replace("http://starshare.live:8080", serverUrl)
}.apply {
    Logger.debug("uri=[${this@alternateUri}], alternateUri=[$this]")
}