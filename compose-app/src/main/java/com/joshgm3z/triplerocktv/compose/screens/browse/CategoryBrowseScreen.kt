package com.joshgm3z.triplerocktv.compose.screens.browse

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.StreamItem
import com.joshgm3z.triplerocktv.compose.screens.common.CloseButton
import com.joshgm3z.triplerocktv.compose.screens.common.DarkLandscapePreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.screens.common.appTopPadding
import com.joshgm3z.triplerocktv.compose.screens.common.gridSpacing
import com.joshgm3z.triplerocktv.compose.screens.common.isLandscape
import com.joshgm3z.triplerocktv.core.util.sampleSeriesList
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.sampleVodList
import com.joshgm3z.triplerocktv.core.util.withComma
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
            is CatalogueUiState.VideoOnDemand -> CategoryBrowse(
                uiState = it,
                onStreamDataClick = {
                    navigateMain(NavMainDestination.Details(it.streamId, StreamType.VideoOnDemand))
                },
                onBackClick = onBackClick,
            )

            is CatalogueUiState.Series -> CategoryBrowse(
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
private fun CategoryBrowse(
    uiState: CatalogueUiState,
    onStreamDataClick: (StreamData) -> Unit = {},
    onSeriesClick: (SeriesStream) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    when (uiState) {
        is CatalogueUiState.VideoOnDemand -> {
            val lazyPagingItems = uiState.pagingStreams.collectAsLazyPagingItems()
            VerticalGrid(
                title = uiState.categoryName,
                onBackClick = onBackClick,
                getStreamItems = {
                    streamDataItems(
                        lazyPagingItems = lazyPagingItems,
                        onClick = onStreamDataClick
                    )
                },
                count = uiState.count
            )
        }

        is CatalogueUiState.Series -> {
            val lazyPagingItems = uiState.pagingStreams.collectAsLazyPagingItems()
            VerticalGrid(
                title = uiState.categoryName,
                onBackClick = onBackClick,
                getStreamItems = {
                    seriesItems(
                        lazyPagingItems = lazyPagingItems,
                        onClick = onSeriesClick
                    )
                },
                count = uiState.count
            )
        }

        else -> return
    }
}

@Composable
fun VerticalGrid(
    title: String,
    count: Int,
    onBackClick: () -> Unit = {},
    getStreamItems: LazyGridScope.() -> Unit = {},
) {
    val scrollState = rememberLazyGridState()
    val isScrolled by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0
                    || scrollState.firstVisibleItemScrollOffset > 0
        }
    }

    Box {
        val appTopPadding = appTopPadding()
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(if (isLandscape()) 6 else 3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = appHorizontalPadding)
        ) {
            gridSpacing(appTopPadding + 40.dp)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = title,
                        color = textColor(),
                        style = typography.headlineLarge,
                    )
                    Text(
                        text = "${count.withComma()} ${if (count > 1) "videos" else "video"}",
                        color = subTextColor(),
                        style = typography.bodyMedium,
                    )
                }
            }
            getStreamItems()
            gridSpacing()
        }
        Header(
            title = title,
            onBackClick = onBackClick,
            showTitle = isScrolled
        )
    }
}

fun LazyGridScope.streamDataItems(
    lazyPagingItems: LazyPagingItems<StreamData>,
    onClick: (StreamData) -> Unit = {}
) {
    items(
        count = lazyPagingItems.itemCount,
        key = lazyPagingItems.itemKey { it.streamId }
    ) { index ->
        lazyPagingItems[index]?.let {
            StreamItem(
                stream = it,
                onStreamClick = { onClick(it) }
            )
        }
    }
}

fun LazyGridScope.seriesItems(
    lazyPagingItems: LazyPagingItems<SeriesStream>,
    onClick: (SeriesStream) -> Unit = {}
) {
    items(
        count = lazyPagingItems.itemCount,
        key = lazyPagingItems.itemKey { it.seriesId }
    ) { index ->
        lazyPagingItems[index]?.let {
            StreamItem(
                stream = it,
                onStreamClick = { onClick(it) }
            )
        }
    }

}

@Composable
fun Header(
    title: String,
    onBackClick: () -> Unit = {},
    showTitle: Boolean = true
) {
    val modifier = if (!showTitle) Modifier
    else Modifier.background(brush = headerGradientBrush())
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = appTopPadding(),
                start = 10.dp, end = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton { onBackClick() }
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Text(
                text = title,
                style = typography.titleLarge,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun headerGradientBrush() = Brush.verticalGradient(
    colors = listOf(
        Color.Black,
        Color.Black.copy(alpha = 0.9f),
        Color.Black.copy(alpha = 0.8f),
    ),
    startY = 0.1f
)

@DarkPreview
@Composable
private fun PreviewCategoryBrowse_Vod() {
    DarkSurface {
        CategoryBrowse(
            uiState = CatalogueUiState.VideoOnDemand(
                categoryName = "Category name",
                pagingStreams = MutableStateFlow(PagingData.from(sampleVodList)),
                count = 1235,
            ),
        )
    }
}

@DarkLandscapePreview
@Composable
private fun PreviewCategoryBrowse_Vod_Landscape() {
    DarkSurface {
        CategoryBrowse(
            uiState = CatalogueUiState.VideoOnDemand(
                categoryName = "Category name",
                pagingStreams = MutableStateFlow(PagingData.from(sampleVodList)),
                count = 1235,
            ),
        )
    }
}

//@DarkPreview
@Composable
private fun PreviewCategoryBrowse_Series() {
    DarkSurface {
        CategoryBrowse(
            uiState = CatalogueUiState.Series(
                categoryName = "Category name",
                pagingStreams = MutableStateFlow(PagingData.from(sampleSeriesList)),
                count = 123,
            ),
        )
    }
}