package com.joshgm3z.triplerocktv.viewmodel

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    var selectedTopbarItem: TopbarItem? = null,
    val contentMap: Map<CategoryEntity, List<StreamEntity>> = emptyMap(),
    var isLoading: Boolean = true,
    var errorMessage: String? = null,
    var username: String? = null,
)

enum class TopbarItem {
    Home,
    VideoOnDemand,
    LiveTV,
    EPG,
    Series,
}

interface IHomeViewModel {
    val uiState: StateFlow<HomeUiState>
    fun onTopbarItemUpdate(topbarItem: TopbarItem)
}