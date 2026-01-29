package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.defaultAnimationSpec

@Composable
fun NavigationContent(
    uiState: HomeUiState,
    openMediaInfoScreen: (StreamEntity) -> Unit,
): @Composable () -> Unit = {
    Crossfade(uiState, animationSpec = defaultAnimationSpec) {
        with(it) {
            when {
                isLoading -> InfoBox(text = "Loading data", delayMs = 0)
                !errorMessage.isNullOrEmpty() -> InfoBox("Error loading content")
                streamEntities.isNotEmpty() -> Content(
                    onContentClick = { streamEntity -> openMediaInfoScreen(streamEntity) },
                    streamEntities = streamEntities,
                )

                else -> InfoBox("No data found")
            }
        }
    }
}