package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.joshgm3z.triplerocktv.compose.R
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.GlidePic
import com.joshgm3z.triplerocktv.compose.screens.settings.SettingScaffold
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.sampleLiveTvList
import com.joshgm3z.triplerocktv.core.util.sampleVodList
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LiveTvCatalogue(
    uiState: CatalogueUiState.LiveTv = CatalogueUiState.LiveTv(),
    onStreamDataClick: (StreamData) -> Unit = {},
    onStreamDataLongClick: (StreamData) -> Unit = {},
    selectedStreamId: Int? = null,
    onBackClick: () -> Unit = {},
) {
    val streamDataList by uiState.streamDataList.collectAsState(emptyList())
    var selectedSteamData by remember { mutableStateOf<StreamData?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(streamDataList) {
        if (selectedSteamData == null && streamDataList.isNotEmpty()) {
            val targetIndex = if (selectedStreamId != null) streamDataList.indexOfFirst {
                it.streamId == selectedStreamId
            } else 0
            require(targetIndex != -1)

            selectedSteamData = streamDataList.getOrNull(targetIndex)
            listState.scrollToItem(targetIndex)
        }
    }

    SettingScaffold(
        title = uiState.categoryName,
        onBackClick = onBackClick
    ) {
        val url = if (LocalInspectionMode.current) ""
        else selectedSteamData?.videoUrl(uiState.userInfo!!)
        TvBox(url)
        Spacer(Modifier.size(20.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = cardColor())
        ) {
            itemsIndexed(streamDataList) { index, item ->
                ChannelItem(
                    streamData = item,
                    index = index + 1,
                    selected = item.streamId == selectedSteamData?.streamId,
                    onClick = {
                        if (selectedSteamData == item) {
                            onStreamDataClick(item)
                        } else {
                            selectedSteamData = item
                        }
                    },
                    onLongPress = {
                        onStreamDataLongClick(item)
                    }
                )
                if (index < streamDataList.size - 1) HorizontalDivider()
            }
        }
    }
}

@Composable
fun TvBox(videoUrl: String?) {
    Logger.debug("videoUrl = [${videoUrl}]")
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val exoPlayer: ExoPlayer? = remember {
        if (!isPreview) ExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
            }
        else null
    }

    LaunchedEffect(videoUrl) {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()

        val mediaItem = MediaItem.fromUri(videoUrl ?: "")
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.stop()
            exoPlayer?.clearMediaItems()
            exoPlayer?.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                keepScreenOn = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun ChannelItem(
    streamData: StreamData,
    index: Int? = null,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    var status by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(status) {
        if (status != null) {
            delay(2000)
            status = null
        }
    }
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { _ ->
                            status = if (streamData.favorite) "Removed from favorites"
                            else "Added to favorites"
                            onLongPress()
                        },
                        onTap = { _ ->
                            onClick()
                        }
                    )
                }
                .background(
                    color = if (selected) colorScheme.onPrimaryFixed
                    else Color.Transparent
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            index?.let {
                Text(
                    text = it.toString(),
                    style = typography.labelLarge,
                    color = textColor(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
                Spacer(Modifier.size(10.dp))
            }
            GlidePic(
                model = streamData.streamIcon,
                defaultDrawable = R.drawable.outline_live_tv_24,
                modifier = Modifier
                    .height(40.dp)
                    .width(45.dp)
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = streamData.name,
                maxLines = 1,
                style = typography.bodyMedium,
                color = textColor(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (streamData.favorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                AnimatedVisibility(
                    visible = selected,
                    enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = status != null,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(brush = darkOverlayBrush())
                    .padding(start = 130.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = status ?: "",
                    style = typography.labelLarge,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun darkOverlayBrush() = Brush.horizontalGradient(
    colors = listOf(
        Color.Transparent,
        colorScheme.background,
        colorScheme.background,
    ),
)

@DarkPreview
@Composable
private fun PreviewChannelItem() {
    DarkSurface {
        ChannelItem(
            streamData = sampleVodList.first(),
            selected = true
        )
    }
}

//@DarkPreview
@Composable
private fun PreviewLiveTvCatalogue() {
    DarkSurface {
        LiveTvCatalogue(
            uiState = CatalogueUiState.LiveTv(
                categoryName = "Live Tv",
                streamDataList = MutableStateFlow(sampleLiveTvList)
            )
        )
    }
}