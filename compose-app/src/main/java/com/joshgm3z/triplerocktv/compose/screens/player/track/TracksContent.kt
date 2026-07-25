package com.joshgm3z.triplerocktv.compose.screens.player.track

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.joshgm3z.triplerocktv.compose.screens.common.DarkLandscapePreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.util.languageName
import com.joshgm3z.triplerocktv.core.viewmodel.ListState
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.core.viewmodel.TrackType

@Composable
fun AudioTracks(
    listState: ListState.AudioTracks,
    onClick: (TrackInfo) -> Unit = {},
) {
    TracksContent(list = listState.list, onClick = onClick)
}

@Composable
fun SubtitleTracks(
    listState: ListState.SubtitleTracks,
    onClick: (TrackInfo) -> Unit = {},
) {
    TracksContent(list = listState.list, onClick = onClick)
}

@Composable
private fun TracksContent(
    list: List<TrackInfo>,
    onClick: (TrackInfo) -> Unit = {},
) {
    var selectedTrackInfo by remember {
        mutableStateOf(
            list.firstOrNull { it.isSelected }
        )
    }
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = cardColor())
    ) {
        itemsIndexed(list) { index, item ->
            TrackItem(
                trackInfo = item,
                selected = selectedTrackInfo?.id == item.id,
                onClick = {
                    selectedTrackInfo = item
                    onClick(item)
                }
            )
            if (index < list.size - 1) {
                HorizontalDivider(Modifier.alpha(0.5f))
            }
        }
    }
}

@Composable
private fun TrackItem(
    trackInfo: TrackInfo,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable(true) {
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = if (trackInfo.disableTrack) "Disabled"
                else trackInfo.language.languageName(),
                color = textColor(),
            )
            if (!trackInfo.disableTrack) Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (trackInfo.id.contains("online")) Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = subTextColor(),
                    modifier = Modifier
                        .size(15.dp)
                )
                Text(
                    text = trackInfo.label ?: "Unknown",
                    color = subTextColor(),
                    style = typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@DarkLandscapePreview
@Composable
private fun PreviewTracksContent() {
    DarkSurface {
        SubtitleTracks(
            listState = ListState.SubtitleTracks(
                listOf(
                    TrackInfo(
                        trackType = TrackType.Subtitle,
                        label = "Wonder.Women.1994.2004 HDRip",
                        language = "de"
                    ),
                    TrackInfo(
                        trackType = TrackType.Subtitle,
                        label = "Wonder.Women.1994.2004 HDRip",
                        language = "en",
                        id = "online"
                    ),
                    TrackInfo(trackType = TrackType.Subtitle).apply {
                        disableTrack = true
                    },
                )
            )
        )
    }
}