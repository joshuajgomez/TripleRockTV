package com.joshgm3z.triplerocktv.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    var selectedBrowseType: BrowseType? = null,
    val contentMap: Map<CategoryEntity, List<StreamEntity>> = emptyMap(),
    val browseMap: Map<BrowseType, List<CategoryEntity>> = mapOf(
        BrowseType.VideoOnDemand to emptyList(),
        BrowseType.Series to emptyList(),
        BrowseType.EPG to emptyList(),
        BrowseType.LiveTV to emptyList(),
    ),
    var isLoading: Boolean = true,
    var errorMessage: String? = null,
    var username: String? = null,
)

enum class BrowseType {
    Home,
    VideoOnDemand,
    LiveTV,
    EPG,
    Series,
}

@HiltViewModel
class BrowseViewModel
@Inject constructor(
    private val repository: MediaLocalRepository,
    localDatastore: LocalDatastore
) : ViewModel() {
    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onTopbarItemUpdate(BrowseType.Home)
        viewModelScope.launch {
            localDatastore.getLoginCredentials { userInfo ->
                _uiState.update { it.copy(username = userInfo.username) }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(browseMap = repository.fetchAllCategories()) }
        }
    }

    fun onTopbarItemUpdate(browseType: BrowseType) {
        Logger.debug("topbarItem=$browseType")
        if (_uiState.value.selectedBrowseType == browseType) {
            Logger.debug("topbarItem already selected")
            return
        }
        _uiState.update { BrowseUiState(selectedBrowseType = browseType) }

        viewModelScope.launch {
            repository.fetchCategories(
                browseType = browseType,
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
        viewModelScope.launch(Dispatchers.IO) {
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