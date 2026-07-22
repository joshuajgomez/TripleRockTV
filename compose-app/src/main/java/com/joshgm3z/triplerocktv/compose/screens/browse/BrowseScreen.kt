package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.LiveUiState
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SeriesUiState
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.VodUiState
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseViewModel

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel = hiltViewModel(),
    navigateMain: (NavMainDestination) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.onViewResume()
    }
    viewModel.uiState.collectAsState().value.let { uiState ->
        BrowseScreenContent(
            uiState = uiState,
            onStreamClick = { streamId, streamType ->
                navigateMain(NavMainDestination.Details(streamId, streamType))
            },
            onSyncClick = {
                navigateMain(NavMainDestination.MediaSync)
            },
            onCategoryClick = {
                navigateMain(
                    NavMainDestination.StreamCatalogue(
                        categoryId = it.categoryId,
                        streamType = it.streamType
                    )
                )
            },
            onLiveCategoryClick = { categoryId, selectedStreamId ->
                navigateMain(
                    NavMainDestination.StreamCatalogue(
                        categoryId = categoryId,
                        selectedStreamId = selectedStreamId,
                        streamType = StreamType.LiveTV
                    )
                )
            }
        )
    }
}

@Composable
private fun BrowseScreenContent(
    uiState: BrowseUiState,
    onStreamClick: (Int, StreamType) -> Unit = { _, _ -> },
    onCategoryClick: (CategoryData) -> Unit = {},
    onLiveCategoryClick: (categoryId: Int, selectedStreamId: Int?) -> Unit = { _, _ -> },
    onSyncClick: () -> Unit = {},
) {
    when (uiState) {
        is BrowseUiState.Empty -> MediaSyncShortcut(
            onSyncClick = onSyncClick,
            streamType = uiState.streamType
        )

        is BrowseUiState.VideoOnDemandState -> VodUiState(
            uiState = uiState,
            onCategoryClick = onCategoryClick,
            onStreamClick = onStreamClick
        )

        is BrowseUiState.LiveTvState -> LiveUiState(
            uiState = uiState,
            onCategoryClick = onLiveCategoryClick,
        )

        is BrowseUiState.SeriesStreamState -> SeriesUiState(
            uiState = uiState,
            onCategoryClick = onCategoryClick,
            onStreamClick = onStreamClick
        )

        else -> return
    }
}

@Composable
fun DarkOverlay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        colorScheme.background.copy(alpha = 0.8f),
                        colorScheme.background
                    ),
                    // Start the gradient at 40% of the container height
                    startY = 0.1f
                )
            )
    )
}

