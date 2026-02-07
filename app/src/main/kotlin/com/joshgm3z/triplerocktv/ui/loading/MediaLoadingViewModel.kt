package com.joshgm3z.triplerocktv.ui.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MediaLoadingUiState {
    object Initial : MediaLoadingUiState()
    data class Update(
        val map: Map<MediaLoadingType, LoadingState> = emptyMap()
    ) : MediaLoadingUiState()

    data class Error(val message: String, val summary: String) : MediaLoadingUiState()
}

@HiltViewModel
class MediaLoadingViewModel
@Inject constructor(
    private val repository: MediaOnlineRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MediaLoadingUiState>(MediaLoadingUiState.Initial)
    val uiState = _uiState.asStateFlow()

    init {
        fetchContent()
    }

    fun fetchContent() {
        viewModelScope.launch {
            delay(1000)
            repository.fetchContent(
                onFetch = { type, state ->
                    with(_uiState.value) {
                        val updatedMap = when (this) {
                            is MediaLoadingUiState.Update -> {
                                Logger.debug("fetchContent: found map $map")
                                if (!map.containsKey(type)) return@with
                                map.toMutableMap()
                                    .apply { set(type, state) }
                            }

                            else -> {
                                Logger.debug("fetchContent: new map")
                                val map = hashMapOf(
                                    MediaLoadingType.VideoOnDemand to LoadingState(),
                                    MediaLoadingType.Series to LoadingState(),
                                    MediaLoadingType.LiveTv to LoadingState(),
                                    MediaLoadingType.EPG to LoadingState(),
                                )
                                map.apply { set(type, state) }
                            }
                        }
                        _uiState.value = MediaLoadingUiState.Update(updatedMap)
                    }
                },
                onError = { errorMessage, summary ->
                    _uiState.value = MediaLoadingUiState.Error(errorMessage, summary)
                }
            )
        }
    }
}