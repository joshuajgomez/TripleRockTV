package com.joshgm3z.triplerocktv.ui.player

import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMediaInfoViewModel : IMediaInfoViewModel {
    override val uiState = MutableStateFlow(
        MediaUiState(
            StreamEntity.sample()
        )
    ).asStateFlow()
}