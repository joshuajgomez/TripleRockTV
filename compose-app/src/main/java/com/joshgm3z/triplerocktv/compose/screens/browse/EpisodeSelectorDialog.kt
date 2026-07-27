package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.CloseButton
import com.joshgm3z.triplerocktv.compose.screens.common.CustomHorizontalDivider
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.GlidePic
import com.joshgm3z.triplerocktv.compose.screens.common.MetadataBar
import com.joshgm3z.triplerocktv.compose.screens.common.listSpacing
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.selectedBgColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.data.EpisodeInfo
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.util.asTwoDigit
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.core.viewmodel.EpisodeSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorUiState

@Composable
fun EpisodeSelectorDialog(
    viewModel: EpisodeSelectorViewModel = hiltViewModel(),
    onBackPress: () -> Unit = {},
    navigateToPlayer: (episodeId: Int, seriesId: Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    EpisodeSelectorDialogContent(
        uiState = uiState,
        onBackPress = onBackPress,
        onSeasonClick = { viewModel.onSeasonSelected(it.number) },
        onEpisodeClick = { navigateToPlayer(it.id, viewModel.seriesId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeSelectorDialogContent(
    uiState: SeriesSelectorUiState,
    onBackPress: () -> Unit = {},
    onSeasonClick: (Season) -> Unit = {},
    onEpisodeClick: (Episode) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onBackPress,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        Box {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                listSpacing(125.dp)
                itemsIndexed(uiState.episodes) { index, item ->
                    EpisodeCard(
                        episode = item,
                        selected = uiState.selectedEpisodeNumber == item.episode_num
                    ) {
                        onEpisodeClick(item)
                    }
                    CustomHorizontalDivider(index, uiState.episodes.size)
                }
                listSpacing()
            }
            Column(
                modifier = Modifier
                    .background(color = colorScheme.surfaceContainerLow.copy(alpha = 0.8f))
                    .padding(vertical = 20.dp)
            ) {
                Topbar(onBackPress)
                Spacer(Modifier.size(20.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listSpacing(10.dp)
                    items(uiState.seasons) {
                        SeasonChip(
                            season = it,
                            selected = uiState.selectedSeasonNumber == it.number
                        ) {
                            onSeasonClick(it)
                        }
                    }
                    listSpacing(10.dp)
                }
            }
        }
    }
}

@Composable
fun EpisodeCard(
    episode: Episode,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable(true) { onClick() }
            .background(
                color = if (selected) selectedBgColor()
                else Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        GlidePic(
            model = episode.episodeInfo?.movie_image,
            modifier = Modifier
                .width(60.dp)
                .height(100.dp)
                .padding(top = 0.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
            Text(
                text = episode.title,
                color = textColor(),
                style = typography.titleMedium
            )
            val episodeLabel = "S${episode.season.asTwoDigit()}E${episode.episode_num}"
            MetadataBar(
                rating = episode.episodeInfo?.rating?.parseToFloat(),
                list = listOf(
                    episodeLabel,
                    episode.totalDurationMs().toTextTime()
                ),
                style = typography.labelMedium
            )
            episode.episodeInfo?.plot?.let {
                Text(
                    text = it,
                    style = typography.bodyMedium,
                    color = subTextColor()
                )
            }
        }
    }
}

@Composable
fun SeasonChip(
    season: Season,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = season.name,
        color = textColor(),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(true) { onClick() }
            .background(color = if (selected) selectedBgColor() else cardColor())
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
fun Topbar(onBackPress: () -> Unit) {
    Box(
        modifier = Modifier.padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        CloseButton { onBackPress() }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Select episode",
                style = typography.titleMedium
            )
        }
    }
}

@DarkPreview
@Composable
private fun PreviewEpisodeSelectorDialog() {
    DarkSurface {
        EpisodeSelectorDialogContent(
            uiState = sampleUiState
        )
    }
}

@DarkPreview
@Composable
private fun PreviewEpisodeCard() {
    DarkSurface {
        EpisodeCard(
            episode = Episode(
                id = 1,
                episode_num = 1,
                title = "Episode 1",
                container_extension = "",
                season = 1,
                episodeInfo = EpisodeInfo(
                    plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                    rating = "3.5f"
                ),
                added = ""
            )
        )
    }
}

private val sampleUiState = SeriesSelectorUiState(
    selectedEpisodeNumber = 2,
    selectedSeasonNumber = 2,
    seasons = listOf(
        Season(
            number = 1,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
        Season(
            number = 2,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
        Season(
            number = 3,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
        Season(
            number = 4,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
        Season(
            number = 5,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
        Season(
            number = 6,
            episodes = listOf(),
            name = "Season 1",
            overview = "",
            coverImageUrl = "",
            voteAverage = 3f
        ),
    ),
    episodes = listOf(
        Episode(
            id = 1,
            episode_num = 1,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
        Episode(
            id = 1,
            episode_num = 2,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
        Episode(
            id = 1,
            episode_num = 3,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
        Episode(
            id = 1,
            episode_num = 4,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
        Episode(
            id = 1,
            episode_num = 5,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
        Episode(
            id = 1,
            episode_num = 6,
            title = "Episode 1",
            container_extension = "",
            season = 1,
            episodeInfo = EpisodeInfo(
                plot = "This is a long plot explaining many thing one by one, or in order whatever works",
                rating = "3.5f"
            ),
            added = ""
        ),
    )
)