package com.joshgm3z.triplerocktv.compose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SectionTitle
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.StreamItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.sampleStreamDataList
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appTopPadding
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navigate: (NavMainDestination) -> Unit = {},
    onBackClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    val uiState by viewModel.uiState.collectAsState()
    SearchScreenContent(
        uiState = uiState,
        onSearchInputChange = { viewModel.onSearchInputChange(it) },
        onStreamClick = { streamId, streamName, streamType ->
            viewModel.saveSearchHint(streamName)
            navigate(NavMainDestination.Details(streamId, streamType))
        },
        onBackClick = onBackClick
    )
}

@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    onSearchInputChange: (String) -> Unit = {},
    onStreamClick: (
        streamId: Int,
        streamName: String,
        streamType: StreamType
    ) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = appTopPadding)
    ) {
        TopPart(
            text = text,
            onSearchInputChange = {
                text = it
                onSearchInputChange(it)
            },
            onBackClick = onBackClick
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = appHorizontalPadding)
        ) {
            if (uiState.showRecentAddedTitle) {
                items(
                    items = uiState.searchHints,
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    SearchHintUi(it) {
                        text = it
                        onSearchInputChange(it)
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    title = when {
                        uiState.showRecentAddedTitle -> "Popular searches"
                        else -> "Search result"
                    },
                    showLoading = uiState.loading
                )
            }
            items(uiState.streams) {
                StreamItem(it, onStreamClick = {
                    when (it) {
                        is StreamData -> onStreamClick(
                            it.streamId,
                            it.name,
                            it.streamType
                        )

                        is SeriesStream -> onStreamClick(
                            it.seriesId,
                            it.name,
                            StreamType.Series
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun SearchHintUi(
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .clickable(true) { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = colorScheme.onBackground.copy(alpha = 0.5f)
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            color = color,
            style = typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowOutward,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TopPart(
    text: String,
    onSearchInputChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(25.dp),
            tint = colorScheme.primary
        )
        TextField(
            value = text,
            onValueChange = {
                onSearchInputChange(it)
            },
            placeholder = {
                Text(
                    text = "Search movies, series and live tv",
                    color = colorScheme.onBackground.copy(alpha = 0.3f)
                )
            },
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = colorScheme.background,
                focusedContainerColor = colorScheme.background,
                unfocusedIndicatorColor = colorScheme.background,
                focusedIndicatorColor = colorScheme.background,
                unfocusedTextColor = textColor(),
                focusedTextColor = textColor(),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            text = "Cancel",
            style = typography.titleMedium,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .clickable(true) {
                    onBackClick()
                }
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen() {
    DarkSurface {
        SearchScreenContent(
            uiState = SearchUiState(
                searchHints = listOf(
                    "Search hint 1",
                    "Search hint 2",
                    "Search hint 3",
                    "Search hint 4",
                    "Search hint 5",
                ),
                streams = sampleStreamDataList().subList(0, 6)
                        + sampleStreamDataList(StreamType.LiveTV).subList(0, 3)
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen_Empty() {
    DarkSurface {
        SearchScreenContent(
            uiState = SearchUiState(
                searchHints = listOf(
                    "Search hint 1",
                    "Search hint 2",
                    "Search hint 3",
                    "Search hint 4",
                    "Search hint 5",
                ),
                statusText = "Searching",
                streams = sampleStreamDataList().subList(0, 6)
                        + sampleStreamDataList(StreamType.LiveTV).subList(0, 3)
            )
        )
    }
}