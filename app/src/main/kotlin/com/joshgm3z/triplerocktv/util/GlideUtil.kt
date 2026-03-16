package com.joshgm3z.triplerocktv.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.joshgm3z.triplerocktv.BuildConfig
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlideUtil
@Inject constructor(
    scope: CoroutineScope,
    private val localDatastore: LocalDatastore,
    @param:ApplicationContext private val context: Context,
) {
    private var serverUrl = ""

    init {
        scope.launch {
            serverUrl = localDatastore.getUserInfo()?.webUrl ?: ""
        }
    }

    fun loadImage(url: String?, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url.alternateUri(serverUrl).orSampleIfDemo())
            .centerCrop()
            .into(imageView)
    }

    fun loadBitmap(
        uri: String?,
        blur: Boolean = false,
        onBitmapReady: (Bitmap) -> Unit,
    ) {
        var glide = Glide.with(context)
            .asBitmap()
            .load(uri.alternateUri(serverUrl).orSampleIfDemo())

        if (blur) glide = glide.apply(
            RequestOptions.bitmapTransform(BlurTransformation(25, 3))
        )

        glide.into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                onBitmapReady(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
    }

    private fun String?.orSampleIfDemo(): Any? {
        return when (BuildConfig.FLAVOR) {
            "demo" -> AppCompatResources.getDrawable(context, R.drawable.avatar_movie)?.toBitmap()
            else -> this
        }
    }
}

fun String?.alternateUri(serverUrl: String): String? = when {
    this == null -> null
    else -> replace("http://starshare.live:8080", serverUrl)
}.apply {
    Logger.debug("uri=[${this@alternateUri}], alternateUri=[$this]")
}