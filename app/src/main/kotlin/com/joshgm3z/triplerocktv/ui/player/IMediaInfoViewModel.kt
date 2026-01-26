package com.joshgm3z.triplerocktv.ui.player

import kotlinx.coroutines.flow.StateFlow

interface IMediaInfoViewModel {
    val uiState: StateFlow<MediaUiState>
}