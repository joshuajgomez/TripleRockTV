package com.joshgm3z.triplerocktv.viewmodel

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryEntity> = emptyList(),
    val mediaList: List<StreamEntity> = emptyList(),
)

interface IHomeViewModel {
    val uiState: StateFlow<HomeUiState>
    fun fetchContent(categoryId: Int)
}