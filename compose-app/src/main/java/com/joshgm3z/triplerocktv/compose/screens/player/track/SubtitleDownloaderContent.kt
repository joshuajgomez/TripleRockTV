package com.joshgm3z.triplerocktv.compose.screens.player.track

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.joshgm3z.triplerocktv.compose.screens.common.CustomHorizontalDivider
import com.joshgm3z.triplerocktv.compose.screens.common.DarkLandscapePreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.util.languageName
import com.joshgm3z.triplerocktv.core.viewmodel.ListState

@Composable
fun SubtitleDownloaderContent(
    listState: ListState.OnlineSubtitleTracks,
    onClick: (SubtitleData) -> Unit = {},
    onLanguageClick: (String?) -> Unit = {},
) {
    Column {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            items(mutableListOf("All").apply { addAll(listState.languages) }) {
                val chipShape = RoundedCornerShape(5.dp)
                val selected = listState.selectedLanguage == it
                        || (listState.selectedLanguage == null && it == "All")
                Text(
                    text = it.languageName(),
                    color = if (selected) colorScheme.primary else colorScheme.onSurface,
                    style = typography.bodySmall,
                    modifier = Modifier
                        .clip(chipShape)
                        .clickable(!selected) {
                            onLanguageClick(if (it == "All") null else it)
                        }
                        .background(color = colorScheme.surfaceVariant)
                        .border(
                            width = if (selected) 1.dp else 0.dp,
                            color = if (selected) colorScheme.outline else Color.Transparent,
                            shape = chipShape
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }
        }
        Spacer(Modifier.size(10.dp))
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = cardColor())
        ) {
            itemsIndexed(listState.list) { index, item ->
                SubtitleResultItem(item) {
                    onClick(item)
                }
                CustomHorizontalDivider(index, listState.list.size)
            }
        }
    }
}

@Composable
fun SubtitleResultItem(
    subtitleData: SubtitleData,
    onClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .clickable(true) {
                onClick()
            }
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 8.dp)
    ) {
        val (titleRef, languageRef, downloadCountRef, iconRef) = createRefs()
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .background(color = colorScheme.onPrimary, shape = CircleShape)
                .padding(3.dp)
                .size(20.dp)
        )
        Text(
            text = subtitleData.title,
            maxLines = 1,
            style = typography.bodyMedium,
            color = textColor(),
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(iconRef.end, margin = 15.dp)
            }
        )
        val subTextStyle = typography.bodySmall
        Text(
            text = subtitleData.language.languageName(),
            color = colorScheme.primary,
            style = subTextStyle,
            modifier = Modifier
                .constrainAs(languageRef) {
                    top.linkTo(titleRef.bottom, margin = 3.dp)
                    start.linkTo(titleRef.start)
                }
                .background(
                    color = colorScheme.primaryContainer,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp)
        )
        Row(
            modifier = Modifier.constrainAs(downloadCountRef) {
                top.linkTo(languageRef.top)
                start.linkTo(languageRef.end, margin = 5.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = colorScheme.primaryContainer
            )
            Text(
                text = subtitleData.downloadCount.toString(),
                style = subTextStyle,
                color = colorScheme.primaryContainer,
            )
        }
    }
}

@DarkLandscapePreview
@Composable
private fun PreviewSubtitleDownloaderContent() {
    DarkSurface {
        SubtitleDownloaderContent(
            listState = ListState.OnlineSubtitleTracks(
                listOf(
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                ),
                languages = listOf(
                    "English", " Spanish ", " French "
                ),
                selectedLanguage = null,
            ),
        )
    }
}