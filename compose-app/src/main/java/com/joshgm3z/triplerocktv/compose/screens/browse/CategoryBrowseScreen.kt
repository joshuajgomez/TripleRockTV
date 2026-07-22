package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.settings.SettingScaffold
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.StreamItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.sampleStreamDataList
import com.joshgm3z.triplerocktv.compose.screens.settings.appBottomPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueUiState
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CategoryBrowseScreen(
    viewModel: CatalogueViewModel = hiltViewModel(),
    navigateMain: (NavMainDestination) -> Unit = {},
    selectedStreamId: Int? = null,
    onBackClick: () -> Unit,
) {
    viewModel.uiState.collectAsState().value?.let { it ->
        when (it) {
            is CatalogueUiState.VideoOnDemand -> CategoryBrowseVod(
                uiState = it,
                onStreamDataClick = {
                    navigateMain(NavMainDestination.Details(it.streamId, StreamType.VideoOnDemand))
                },
                onBackClick = onBackClick
            )

            is CatalogueUiState.Series -> CategoryBrowseSeries(
                uiState = it,
                onSeriesClick = {
                    navigateMain(NavMainDestination.Details(it.seriesId, StreamType.Series))
                },
                onBackClick = onBackClick
            )

            is CatalogueUiState.LiveTv -> LiveTvCatalogue(
                uiState = it,
                selectedStreamId = selectedStreamId,
                onStreamDataClick = {
                    navigateMain(NavMainDestination.Playback(it.streamId, StreamType.LiveTV))
                },
                onStreamDataLongClick = {
                    viewModel.updateFavorites(it.streamId, !it.favorite)
                },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun CategoryBrowseVod(
    uiState: CatalogueUiState.VideoOnDemand = CatalogueUiState.VideoOnDemand(),
    onStreamDataClick: (StreamData) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    SettingScaffold(
        title = uiState.categoryName,
        onBackClick = onBackClick,
        applyBottomPadding = false
    ) {
        val lazyPagingItems = uiState.pagingStreams.collectAsLazyPagingItems()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.streamId }
            ) { index ->
                lazyPagingItems[index]?.let {
                    StreamItem(
                        stream = it,
                        onStreamClick = { onStreamDataClick(it) }
                    )
                }
            }
            bottomSpacing()
        }
    }
}

@Composable
private fun CategoryBrowseSeries(
    uiState: CatalogueUiState.Series = CatalogueUiState.Series(),
    onSeriesClick: (SeriesStream) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    SettingScaffold(
        title = uiState.categoryName,
        onBackClick = onBackClick,
        applyBottomPadding = false
    ) {
        val lazyPagingItems = uiState.pagingStreams.collectAsLazyPagingItems()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.seriesId }
            ) { index ->
                lazyPagingItems[index]?.let {
                    StreamItem(
                        stream = it,
                        onStreamClick = { onSeriesClick(it) }
                    )
                }
            }
            bottomSpacing()
        }
    }
}

fun LazyGridScope.bottomSpacing() {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(Modifier.size(appBottomPadding))
    }
}

fun LazyListScope.bottomSpacing() {
    item {
        Spacer(Modifier.size(appBottomPadding))
    }
}

@DarkPreview
@Composable
private fun PreviewCategoryBrowseScreenContent() {
    DarkSurface {
        CategoryBrowseVod(
            uiState = CatalogueUiState.VideoOnDemand(
                categoryName = "Category name",
                pagingStreams = MutableStateFlow(PagingData.from(sampleStreamDataList()))
            )
        )
    }
}