package com.joshgm3z.triplerocktv.viewmodel

import com.joshgm3z.triplerocktv.repository.MediaRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val media: List<MediaData>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel
@Inject constructor(
    repository: MediaRepository
) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        repository.fetchAllMediaData(
            onSuccess = { _uiState.value = HomeUiState.Success(it) },
            onError = { _uiState.value = HomeUiState.Error(it) }
        )
    }
}