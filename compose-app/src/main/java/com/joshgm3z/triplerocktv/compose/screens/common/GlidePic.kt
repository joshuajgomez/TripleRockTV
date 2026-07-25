package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlidePic(
    modifier: Modifier = Modifier,
    model: String?,
    defaultDrawable: Int? = null,
) = GlideImage(
    modifier = modifier.background(color = colorScheme.onBackground.copy(alpha = 0.1f)),
    model = model,
    failure = defaultDrawable?.let { placeholder(it) },
    loading = defaultDrawable?.let { placeholder(it) },
    contentDescription = null,
    contentScale = ContentScale.Crop
)