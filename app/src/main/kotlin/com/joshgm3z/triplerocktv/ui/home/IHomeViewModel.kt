package com.joshgm3z.triplerocktv.ui.home

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.StateFlow

sealed class HomeUiState {
    class Ready(
        val selectedTopbarItem: TopbarItem,
        val selectedCategoryEntity: CategoryEntity,
        val categoryEntities: List<CategoryEntity>,
        val streamEntities: List<StreamEntity>
    ) : HomeUiState()

    data class Loading(val message: String = "Loading content") : HomeUiState()
    data object Empty : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

interface IHomeViewModel {
    val uiState: StateFlow<HomeUiState>
    fun fetchContent(categoryEntity: CategoryEntity)
    fun fetchCategories(topbarItem: TopbarItem)
}