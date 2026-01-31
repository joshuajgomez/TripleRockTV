package com.joshgm3z.triplerocktv.ui.loading

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.login.LoginLayoutId
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SplashScreen() {
    // 1. Define the infinite transition
    val infiniteTransition = rememberInfiniteTransition(label = "flashing")

    // 2. Create an animated alpha value from 0f to 1f
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // duration of one flash
            repeatMode = RepeatMode.Reverse // makes it fade in then fade out
        ),
        label = "alpha"
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.logo_3rocktv_cutout),
            contentDescription = null,
            modifier = Modifier
                .layoutId(LoginLayoutId.Logo)
                .width(200.dp)
                .graphicsLayer(alpha = alpha)
        )
    }
}


@TvPreview
@Composable
private fun PreviewSplashScreen() {
    TripleRockTVTheme {
        SplashScreen()
    }
}