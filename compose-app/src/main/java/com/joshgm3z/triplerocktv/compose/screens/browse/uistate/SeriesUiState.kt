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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState

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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = appHorizontalPadding)
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

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle("All categories")
        }
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.categoryId } // Replace with your unique ID field
        ) { index ->
            val item = lazyPagingItems[index]
            if (item != null) {
                // Your Grid Item Composable
                CategoryItem(
                    categoryData = item,
                    onClick = { onCategoryClick(item) }
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
//                recentPlayedEpisodes = sampleStreamDataList,
//                favorites = sampleStreamDataList
            )
        )
    }
}
