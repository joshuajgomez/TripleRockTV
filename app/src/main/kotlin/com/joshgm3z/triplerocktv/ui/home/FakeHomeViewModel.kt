package com.joshgm3z.triplerocktv.ui.home

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeHomeViewModel : IHomeViewModel {
    override val uiState: StateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState(
            categories = CategoryEntity.samples(),
            mediaList = listOf(
                StreamEntity.sample(),
                StreamEntity.sample(),
                StreamEntity.sample(),
            )
        )
    ).asStateFlow()

    override fun fetchContent(categoryId: Int) {}
}