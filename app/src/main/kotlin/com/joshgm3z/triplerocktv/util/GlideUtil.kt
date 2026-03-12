package com.joshgm3z.triplerocktv.util

import android.widget.ImageView
import com.bumptech.glide.Glide
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
}

fun String?.alternateUri(serverUrl: String): String? = when {
    this == null -> null
    else -> replace("http://starshare.live:8080", serverUrl)
}.apply {
    Logger.debug("uri=[${this@alternateUri}], alternateUri=[$this]")
}