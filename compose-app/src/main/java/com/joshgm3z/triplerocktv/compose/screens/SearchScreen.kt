package com.joshgm3z.triplerocktv.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.headerGradientBrush
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SectionTitle
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.StreamItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.gridSpacing
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appTopPadding
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.sampleLiveTvList
import com.joshgm3z.triplerocktv.core.util.sampleSeriesList
import com.joshgm3z.triplerocktv.core.util.sampleVodList
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
    uiState: SearchUiState?,
    onSearchInputChange: (String) -> Unit = {},
    onStreamClick: (
        streamId: Int,
        streamName: String,
        streamType: StreamType
    ) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    Box {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = appHorizontalPadding)
        ) {
            gridSpacing(140.dp)

            when (uiState) {
                is SearchUiState.Initial -> {
                    items(
                        items = uiState.hints,
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        SearchHintUi(it) {
                            text = it
                            onSearchInputChange(it)
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SectionTitle(
                            title = "Recently added",
                            paddingValues = PaddingValues(0.dp)
                        )
                    }
                    items(uiState.initialStreams) {
                        StreamItem(it, onStreamClick = {
                            when (it) {
                                is StreamData -> onStreamClick(
                                    it.streamId,
                                    it.name,
                                    it.streamType
                                )
                            }
                        })
                    }
                }

                is SearchUiState.Loading -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                }

                is SearchUiState.NoResult -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = "No results found",
                        style = typography.bodyMedium,
                        color = subTextColor(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                is SearchUiState.Result -> {
                    items(uiState.list) {
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

                else -> return@LazyVerticalGrid
            }

            gridSpacing()
        }
        TopPart(
            text = text,
            onSearchInputChange = {
                text = it
                onSearchInputChange(it)
            },
            onBackClick = onBackClick
        )
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
            .padding(horizontal = 10.dp, vertical = 8.dp),
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
    Column(
        modifier = Modifier
            .background(brush = headerGradientBrush())
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 10.dp, end = 10.dp,
                    top = appTopPadding, bottom = 10.dp
                )
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(color = cardColor())
                .padding(start = 15.dp),
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
                        text = "Search anything",
                        color = subTextColor()
                    )
                },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = textColor(),
                    focusedTextColor = textColor(),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row(
                modifier = Modifier
                    .clickable(true) {
                        onBackClick()
                    }
                    .fillMaxHeight()
                    .padding(start = 10.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    style = typography.titleMedium,
                )
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen_Results() {
    DarkSurface {
        SearchScreenContent(
            uiState = SearchUiState.Result(
                list = sampleVodList.subList(0, 3)
                        + sampleLiveTvList.subList(0, 3)
                        + sampleSeriesList.subList(0, 3)
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen_NoResult() {
    DarkSurface {
        SearchScreenContent(uiState = SearchUiState.NoResult)
    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen_Loading() {
    DarkSurface {
        SearchScreenContent(uiState = SearchUiState.Loading)
    }
}

@DarkPreview
@Composable
private fun PreviewSearchScreen_Initial() {
    DarkSurface {
        SearchScreenContent(
            uiState = SearchUiState.Initial(
                hints = listOf(
                    "Search hint 1",
                    "Search hint 2",
                    "Search hint 3",
                    "Search hint 4",
                    "Search hint 5",
                ),
                initialStreams = sampleVodList.subList(0, 6)
            )
        )
    }
}