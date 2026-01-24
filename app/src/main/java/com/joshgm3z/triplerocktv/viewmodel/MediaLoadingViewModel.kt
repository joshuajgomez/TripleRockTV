package com.joshgm3z.triplerocktv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaLoadingUiState(
    val map: Map<MediaLoadingType, LoadingState> = emptyMap(),
) {
    companion object {
        fun sample() = MediaLoadingUiState()
    }
}

@HiltViewModel
class MediaLoadingViewModel
@Inject constructor(
    repository: MediaOnlineRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Map<MediaLoadingType, LoadingState>>(emptyMap())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.fetchContent { type, state ->
                _uiState.update {
                    it.toMutableMap().apply { set(type, state) }
                }
            }
        }
    }
}