package com.joshgm3z.triplerocktv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val repository: MediaLocalRepository
) : ViewModel(), IHomeViewModel {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    override val uiState = _uiState.asStateFlow()

    lateinit var categoryEntities: List<CategoryEntity>
    lateinit var selectedCategoryEntity: CategoryEntity
    var selectedTopbarItem: TopbarItem = TopbarItem.Home // Initial value

    init {
        fetchCategories(selectedTopbarItem)
    }

    override fun fetchCategories(topbarItem: TopbarItem) {
        Logger.debug("topbarItem=$topbarItem")
        selectedTopbarItem = topbarItem
        _uiState.value = HomeUiState.Loading("Loading categories")

        categoryEntities = emptyList()
        viewModelScope.launch {
            repository.fetchCategories(
                topbarItem = topbarItem,
                onSuccess = { categories ->
                    Logger.debug("fetchCategories.onSuccess $categories")
                    categoryEntities = categories
                    if (categories.isEmpty()) {
                        _uiState.value = HomeUiState.Error("No categories found")
                    } else {
                        fetchContent(categoryEntity = categories.first())
                    }
                },
                onError = {
                    Logger.debug("fetchCategories.onError $it")
                    _uiState.value = HomeUiState.Error(it)
                },
            )
        }
    }

    override fun fetchContent(categoryEntity: CategoryEntity) {
        Logger.debug("categoryEntity=$categoryEntity")
        selectedCategoryEntity = categoryEntity
        _uiState.value = HomeUiState.Loading("Loading content")
        viewModelScope.launch {
            repository.fetchAllMediaData(
                categoryId = categoryEntity.categoryId,
                onSuccess = { streams ->
                    Logger.debug("fetchAllMediaData.onSuccess $streams")
                    if (streams.isEmpty()) {
                        _uiState.value = HomeUiState.Error("No contents found")
                    } else {
                        _uiState.value = HomeUiState.Ready(
                            categoryEntities = categoryEntities,
                            streamEntities = streams,
                            selectedCategoryEntity = selectedCategoryEntity,
                            selectedTopbarItem = selectedTopbarItem
                        )
                    }
                },
                onError = {
                    Logger.debug("fetchAllMediaData.onError $it")
                    _uiState.value = HomeUiState.Error(it)
                },
            )
        }
    }
}