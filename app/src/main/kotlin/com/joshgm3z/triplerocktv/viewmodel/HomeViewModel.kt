package com.joshgm3z.triplerocktv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
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
    private val repository: MediaLocalRepository,
    localDatastore: LocalDatastore
) : ViewModel(), IHomeViewModel {
    private val _uiState = MutableStateFlow(HomeUiState())
    override val uiState = _uiState.asStateFlow()

    init {
        onTopbarItemUpdate(TopbarItem.Home)
        viewModelScope.launch {
            localDatastore.getLoginCredentials { userInfo ->
                _uiState.update { it.copy(username = userInfo.username) }
            }
        }
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
                    Logger.debug("fetchCategories.onSuccess $categories")
                    if (categories.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                errorMessage = "No categories found",
                                isLoading = false
                            )
                        }
                    } else {
                        fetchStreams(categories)
                    }
                },
                onError = { error ->
                    Logger.debug("fetchCategories.onError $error")
                    _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                },
            )
        }
    }

    private fun fetchStreams(categories: List<CategoryEntity>) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    contentMap = categories.associateWith { category ->
                        repository.fetchStreams(
                            category.categoryId
                        )
                    })
            }
        }
    }
}