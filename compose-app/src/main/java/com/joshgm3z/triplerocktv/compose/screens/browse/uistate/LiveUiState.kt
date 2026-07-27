package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryItem
import com.joshgm3z.triplerocktv.compose.screens.browse.ChannelItem
import com.joshgm3z.triplerocktv.compose.screens.common.CustomHorizontalDivider
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.sampleVodCategoryList
import com.joshgm3z.triplerocktv.core.util.sampleVodList
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LiveUiState(
    uiState: BrowseUiState.LiveTvState,
    onCategoryClick: (categoryId: Int, selectedStreamId: Int?) -> Unit = { _, _ -> },
) {
    val lazyPagingItems = uiState.pagingCategoryData.collectAsLazyPagingItems()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ChannelRow(
                title = "Continue watching",
                streams = uiState.recentPlayed,
                onStreamClick = {
                    onCategoryClick(
                        it.categoryId,
                        it.streamId
                    )
                }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            ChannelRow(
                title = "Favorites",
                streams = uiState.favorites,
                onStreamClick = {
                    onCategoryClick(
                        it.categoryId,
                        it.streamId
                    )
                }
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
                    onClick = { onCategoryClick(it.categoryId, null) }
                )
            }
        }
    }
}

@Composable
fun ChannelRow(
    title: String,
    streams: List<StreamData>,
    onStreamClick: (StreamData) -> Unit
) {
    if (streams.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(title = title)
        Column(
            modifier = Modifier
                .padding(horizontal = appHorizontalPadding)
                .clip(RoundedCornerShape(10.dp))
                .background(color = cardColor())
        ) {
            streams.forEachIndexed { index, data ->
                ChannelItem(
                    streamData = data,
                    onClick = { onStreamClick(data) },
                )
                CustomHorizontalDivider(index, streams.size)
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewVodUiState() {
    DarkSurface {
        LiveUiState(
            uiState = BrowseUiState.LiveTvState(
                pagingCategoryData = MutableStateFlow(
                    PagingData.from(sampleVodCategoryList)
                ),
                recentPlayed = sampleVodList.subList(0, 3),
                favorites = sampleVodList.subList(0, 3)
            )
        )
    }
}
