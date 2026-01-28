package com.joshgm3z.triplerocktv.ui.home

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    var selectedTopbarItem: TopbarItem? = null,
    var selectedCategoryEntity: CategoryEntity? = null,
    var categoryEntities: List<CategoryEntity> = emptyList(),
    val streamEntities: List<StreamEntity> = emptyList(),
    var isLoading: Boolean = true,
    var errorMessage: String? = null,
)

interface IHomeViewModel {
    val uiState: StateFlow<HomeUiState>
    fun onSelectedCategoryUpdate(categoryEntity: CategoryEntity)
    fun onTopbarItemUpdate(topbarItem: TopbarItem)
}