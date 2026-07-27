package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.joshgm3z.triplerocktv.compose.screens.settings.cardCornerRadius
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.GlidePic
import com.joshgm3z.triplerocktv.compose.screens.common.listSpacing
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayed
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.ifNullOrEmpty
import com.joshgm3z.triplerocktv.core.util.sampleVodList

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RecentStreamRow(
    title: String,
    streams: List<Any> = emptyList(),
    sidePadding: Dp = 10.dp,
    listHorizontalPadding: Dp = 5.dp,
    onStreamClick: (Int, StreamType) -> Unit = { _, _ -> }
) {
    if (streams.isEmpty()) return
    Logger.debug("streams = [$streams]")
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionTitle(title = title)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(listHorizontalPadding)) {
            listSpacing(sidePadding)
            items(streams) {
                RecentStreamItem(
                    stream = it,
                    onStreamClick = {
                        when (it) {
                            is StreamData -> onStreamClick(it.streamId, it.streamType)
                            is SeriesStream -> onStreamClick(it.seriesId, StreamType.Series)
                            else -> return@RecentStreamItem
                        }
                    }
                )
            }
            listSpacing(sidePadding)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RecentStreamItem(
    stream: Any,
    onStreamClick: () -> Unit = {}
) {
    val title = when (stream) {
        is StreamData -> stream.name
        is Episode -> stream.title
        else -> return
    }
    val imageUri = when (stream) {
        is StreamData -> stream.movieMetadata?.backPosterUrl.ifNullOrEmpty(stream.streamIcon)
        is Episode -> stream.episodeInfo?.movie_image
        else -> return
    }
    val progress = when (stream) {
        is StreamData -> stream.progressPercent()
        is Episode -> stream.progressPercent()
        else -> return
    }
    Box(
        modifier = Modifier
            .width(175.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(cardCornerRadius))
            .clickable(true) {
                when (stream) {
                    is StreamData -> onStreamClick()

                    is SeriesStream -> onStreamClick()

                    else -> return@clickable
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        GlidePic(
            model = imageUri,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .background(brush = darkOverlayBrush())
                .padding(start = 8.dp, end = 8.dp, bottom = 10.dp, top = 30.dp)
        ) {
            Text(
                text = title,
                maxLines = 2,
                style = typography.bodyMedium,
            )
            Spacer(Modifier.size(3.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.height(4.dp),
                drawStopIndicator = {}
            )
        }
    }
}

@Composable
private fun darkOverlayBrush() = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        colorScheme.background.copy(alpha = 0.7f),
        colorScheme.background,
    ),
    // Start the gradient at 40% of the container height
    startY = 1f
)

@Composable
@DarkPreview
fun PreviewRecentStreamRow() {
    DarkSurface {
        RecentStreamRow(
            title = "Continue watching",
            streams = sampleVodList.map {
                it.copy(
                    movieMetadata = MovieMetadata(
                        totalDurationMs = 120 * 1000L,
                        description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                    ),
                ).apply {
                    recentlyPlayed = RecentlyPlayed(
                        id = it.streamId,
                        playedDuration = 60 * 1000L,
                        streamType = StreamType.VideoOnDemand,
                        added = 0L
                    )
                }
            }
        )
    }
}
