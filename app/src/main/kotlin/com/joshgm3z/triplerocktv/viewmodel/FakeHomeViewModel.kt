package com.joshgm3z.triplerocktv.viewmodel

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeHomeViewModel : IHomeViewModel {
    override val uiState: StateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState(
            categories = CategoryEntity.samples()
        )
    ).asStateFlow()

    override fun fetchContent(categoryId: Int) {}
}