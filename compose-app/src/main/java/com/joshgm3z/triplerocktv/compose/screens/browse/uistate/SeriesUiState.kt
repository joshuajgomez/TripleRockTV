package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.util.sampleSeriesCategoryList
import com.joshgm3z.triplerocktv.core.util.sampleSeriesList
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SeriesUiState(
    uiState: BrowseUiState.SeriesStreamState,
    onCategoryClick: (CategoryData) -> Unit = {},
    onStreamClick: (Int, StreamType) -> Unit = { _, _ -> }
) {
    val lazyPagingItems = uiState.pagingCategoryData.collectAsLazyPagingItems()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            RecentStreamRow(
                title = "Continue watching",
                streams = uiState.recentPlayedEpisodes,
                onStreamClick = onStreamClick
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            StreamRow(
                title = "Favorites",
                streams = uiState.favorites,
                onStreamClick = onStreamClick
            )
        }

        stickyHeader {
            SectionTitle("All categories")
        }
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.categoryId }
        ) { index ->
            lazyPagingItems[index]?.let {
                CategoryItem(
                    modifier = if (index % 2 == 0) Modifier.padding(start = 15.dp)
                    else Modifier.padding(end = 15.dp),
                    categoryData = it,
                    onClick = { onCategoryClick(it) }
                )
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewSeriesUiState() {
    DarkSurface {
        SeriesUiState(
            uiState = BrowseUiState.SeriesStreamState(
                recentPlayedEpisodes = sampleSeriesList,
                favorites = sampleSeriesList,
                pagingCategoryData = MutableStateFlow(PagingData.from(sampleSeriesCategoryList))
            )
        )
    }
}
