package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryItem
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryRow
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.util.sampleLabelToCategoryMap
import com.joshgm3z.triplerocktv.core.util.sampleVodCategoryList
import com.joshgm3z.triplerocktv.core.util.sampleVodList
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun VodUiState(
    uiState: BrowseUiState.VideoOnDemandState,
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
                streams = uiState.recentPlayed,
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

        items(
            items = uiState.categoryMap.keys.toList(),
            span = { GridItemSpan(maxLineSpan) }) {
            CategoryRow(
                title = it,
                categories = uiState.categoryMap[it]!!,
                onCategoryClick = onCategoryClick
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
private fun PreviewVodUiState() {
    DarkSurface {
        VodUiState(
            uiState = BrowseUiState.VideoOnDemandState(
                categoryMap = sampleLabelToCategoryMap,
                recentPlayed = sampleVodList,
                favorites = sampleVodList,
                pagingCategoryData = MutableStateFlow(PagingData.from(sampleVodCategoryList)),
            )
        )
    }
}
