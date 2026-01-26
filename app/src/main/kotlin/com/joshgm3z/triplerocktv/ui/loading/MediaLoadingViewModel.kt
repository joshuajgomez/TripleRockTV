package com.joshgm3z.triplerocktv.ui.loading

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

sealed class MediaLoadingUiState {
    object Initial : MediaLoadingUiState()
    data class Update(
        val map: Map<MediaLoadingType, LoadingState> = emptyMap()
    ) : MediaLoadingUiState()

    data class Error(val message: String) : MediaLoadingUiState()
}

@HiltViewModel
class MediaLoadingViewModel
@Inject constructor(
    repository: MediaOnlineRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MediaLoadingUiState>(MediaLoadingUiState.Initial)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.fetchContent(
                onFetch = { type, state ->
                    with(_uiState.value) {
                        val updatedMap = when (this) {
                            is MediaLoadingUiState.Update -> map.toMutableMap()
                                .apply { set(type, state) }

                            else -> mapOf(type to state)
                        }
                        _uiState.value = MediaLoadingUiState.Update(updatedMap)
                    }
                },
                onError = { errorMessage ->
                    _uiState.value = MediaLoadingUiState.Error(errorMessage)
                }
            )
        }
    }
}