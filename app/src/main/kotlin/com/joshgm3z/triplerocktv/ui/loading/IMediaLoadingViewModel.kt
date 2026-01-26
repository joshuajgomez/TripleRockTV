package com.joshgm3z.triplerocktv.ui.loading

import kotlinx.coroutines.flow.StateFlow

interface IMediaLoadingViewModel {
    val uiState: StateFlow<MediaLoadingUiState>
    fun fetchContent()
}