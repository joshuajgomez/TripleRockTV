package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray900
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun LoadingBox() {
    FlowRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 10.dp, top = 10.dp),
        maxItemsInEachRow = 5,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        repeat(10) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .shimmerLoading()
                    .width(cardContainerWidth.dp)
                    .height(cardContainerHeight.dp),
            ) { }
        }

    }
}

@TvPreview
@Composable
private fun PreviewLoadingBox() {
    TripleRockTVTheme {
        LoadingBox()
    }
}

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation by transition.animateColor(
        initialValue = colorScheme.background,
        targetValue = Gray900,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    return drawBehind {
        drawRect(
            color = translateAnimation,
        )
    }
}
