package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

@Composable
fun navigationContent(
    uiState: HomeUiState,
    openMediaInfoScreen: (StreamEntity) -> Unit,
): @Composable () -> Unit = {
    with(uiState) {
        when {
            isLoading -> LoadingBox()
            !errorMessage.isNullOrEmpty() -> InfoBox("Error loading content")
            streamEntities.isNotEmpty() -> Content(
                onContentClick = { streamEntity -> openMediaInfoScreen(streamEntity) },
                streamEntities = streamEntities,
            )

            else -> InfoBox("No data found")
        }
    }
}
