package com.joshgm3z.triplerocktv.ui.loading

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeMediaLoadingViewModel : IMediaLoadingViewModel {
    override val uiState: StateFlow<MediaLoadingUiState> =
        MutableStateFlow(MediaLoadingUiState.Initial)

    override fun fetchContent() {}
}