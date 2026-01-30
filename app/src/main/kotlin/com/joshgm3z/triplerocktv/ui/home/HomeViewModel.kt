package com.joshgm3z.triplerocktv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val repository: MediaLocalRepository
) : ViewModel(), IHomeViewModel {
    private val _uiState = MutableStateFlow(HomeUiState())
    override val uiState = _uiState.asStateFlow()

    private var streamEntityCache: HashMap<CategoryEntity, List<StreamEntity>> = hashMapOf()

    init {
        onTopbarItemUpdate(TopbarItem.Home)
    }

    override fun onTopbarItemUpdate(topbarItem: TopbarItem) {
        Logger.debug("topbarItem=$topbarItem")
        if (_uiState.value.selectedTopbarItem == topbarItem) {
            Logger.debug("topbarItem already selected")
            return
        }
        _uiState.update { HomeUiState(selectedTopbarItem = topbarItem) }

        viewModelScope.launch {
            repository.fetchCategories(
                topbarItem = topbarItem,
                onSuccess = { categories ->
                    _uiState.update { it.copy(isLoading = false) }
                    Logger.debug("fetchCategories.onSuccess $categories")
                    if (categories.isEmpty()) {
                        _uiState.update { it.copy(errorMessage = "No categories found") }
                    } else {
                        _uiState.update { it.copy(categoryEntities = categories) }
                        onSelectedCategoryUpdate(categories.first())
                    }
                },
                onError = { error ->
                    Logger.debug("fetchCategories.onError $error")
                    _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                },
            )
        }
    }

    override fun onSelectedCategoryUpdate(categoryEntity: CategoryEntity) {
        Logger.debug("categoryEntity=$categoryEntity")

        if (_uiState.value.selectedCategoryEntity == categoryEntity) {
            Logger.debug("categoryEntity already selected")
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                selectedCategoryEntity = categoryEntity,
                streamEntities = emptyList()
            )
        }

        streamEntityCache[categoryEntity]?.let { streams ->
            Logger.debug("streams available in cache")
            _uiState.update { it.copy(streamEntities = streams, isLoading = false) }
            return
        }

        viewModelScope.launch {
            repository.fetchAllMediaData(
                categoryId = categoryEntity.categoryId,
                onSuccess = { streams ->
                    _uiState.update { it.copy(isLoading = false) }
                    Logger.debug("fetchAllMediaData.onSuccess $streams")
                    if (streams.isEmpty()) {
                        _uiState.update { it.copy(errorMessage = "No contents found") }
                    } else {
                        _uiState.update { it.copy(streamEntities = streams) }
                        streamEntityCache[categoryEntity] = streams
                    }
                },
                onError = { error ->
                    Logger.debug("fetchAllMediaData.onError $error")
                    _uiState.update { it.copy(errorMessage = error, isLoading = false) }
                },
            )
        }
    }
}