package com.joshgm3z.triplerocktv.compose.screens.player.track

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = cardColor())
    ) {
        itemsIndexed(list) { index, item ->
            TrackItem(item) {
                onClick(item)
            }
            if (index < list.size - 1) {
                HorizontalDivider(Modifier.alpha(0.5f))
            }
        }
    }
}

@Composable
private fun TrackItem(
    trackInfo: TrackInfo,
    onClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(true) {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        val (titleRef, languageRef, iconRef) = createRefs()
        RadioButton(
            selected = trackInfo.isSelected,
            onClick = onClick,
            modifier = Modifier
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = trackInfo.language.languageName(),
            color = textColor(),
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(iconRef.end, margin = 10.dp)
            }
        )
        Row(
            modifier = Modifier
                .constrainAs(languageRef) {
                    top.linkTo(titleRef.bottom, margin = 3.dp)
                    start.linkTo(titleRef.start)
                }
                .fillMaxWidth(),
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
            )
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
                )
            )
        )
    }
}