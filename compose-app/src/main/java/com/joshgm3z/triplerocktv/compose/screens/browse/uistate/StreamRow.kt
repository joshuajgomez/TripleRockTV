package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.joshgm3z.triplerocktv.compose.R
import com.joshgm3z.triplerocktv.compose.screens.settings.cardCornerRadius
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.GlidePic
import com.joshgm3z.triplerocktv.compose.screens.common.listSpacing
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.sampleLiveTvList
import com.joshgm3z.triplerocktv.core.util.sampleVodList

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StreamRow(
    title: String = "OSCAR WINNING MOVIES",
    streams: List<Any> = emptyList(),
    sidePadding: Dp = 10.dp,
    listHorizontalPadding: Dp = 5.dp,
    onStreamClick: (Int, StreamType) -> Unit = { _, _ -> }
) {
    if (streams.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(title = title)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(listHorizontalPadding)) {
            listSpacing(sidePadding)
            items(streams) {
                StreamItem(
                    stream = it,
                    onStreamClick = {
                        when (it) {
                            is StreamData -> onStreamClick(it.streamId, it.streamType)
                            is SeriesStream -> onStreamClick(it.seriesId, StreamType.Series)
                            else -> return@StreamItem
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
fun StreamItem(
    stream: Any,
    onStreamClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(125.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(cardCornerRadius)),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (stream is StreamData
            && stream.streamType == StreamType.LiveTV
        ) IconPosterCard(
            stream,
            onStreamClick
        )
        else FullSizePosterCard(stream, onStreamClick)
    }
}

@Composable
fun IconPosterCard(
    stream: StreamData,
    onStreamClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = colorScheme.onBackground.copy(alpha = 0.1f))
            .fillMaxSize()
            .padding(10.dp)
            .clickable(true) { onStreamClick() }
    ) {
        GlidePic(
            model = stream.streamIcon,
            defaultDrawable = R.drawable.outline_live_tv_24,
            modifier = Modifier.size(60.dp),
            emptyBackground = true
        )
        Text(
            text = stream.name,
            maxLines = 3,
            style = typography.titleMedium
        )
    }
}

@Composable
fun FullSizePosterCard(
    stream: Any,
    onStreamClick: () -> Unit
) {
    Box {
        StreamPlaceholder(stream)
        GlidePic(
            model = when (stream) {
                is StreamData -> stream.streamIcon
                is SeriesStream -> stream.coverImageUrl
                else -> return
            },
            defaultDrawable = R.drawable.avatar_movie,
            modifier = Modifier
                .fillMaxSize()
                .clickable(true) {
                    onStreamClick()
                },
        )
    }
}

@Composable
fun StreamPlaceholder(stream: Any) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Icon(
            imageVector = when (stream) {
                is StreamData -> when (stream.streamType) {
                    StreamType.LiveTV -> Icons.Default.LiveTv
                    else -> Icons.Default.Movie
                }

                is SeriesStream -> Icons.Default.OndemandVideo
                else -> return@Column
            },
            tint = colorScheme.primary,
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Text(
            text = when (stream) {
                is StreamData -> stream.name
                is SeriesStream -> stream.name
                else -> return
            },
            maxLines = 3,
            style = typography.titleMedium
        )
    }
}

@Composable
@DarkPreview
fun PreviewStreamItem_Vod() {
    DarkSurface {
        StreamItem(stream = sampleVodList.first())
    }
}

@Composable
@DarkPreview
fun PreviewStreamItem_LiveTv() {
    DarkSurface {
        StreamItem(stream = sampleLiveTvList.first())
    }
}

@Composable
//@DarkPreview
fun PreviewStreamRow() {
    DarkSurface {
        StreamRow(
            title = "OSCAR WINNING MOVIES",
            streams = sampleVodList
        )
    }
}
