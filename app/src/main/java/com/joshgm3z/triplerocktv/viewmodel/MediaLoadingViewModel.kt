package com.joshgm3z.triplerocktv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MediaLoadingType(val label: String) {
    VideoOnDemand("Video On Demand"),
    Series("Series"),
    LiveTv("Live TV"),
    ParsingPlaylist("Parsing Playlist"),
}

data class MediaLoadingUiState(
    val map: Map<MediaLoadingType, LoadingState> = emptyMap(),
) {
    companion object {
        fun sample() = MediaLoadingUiState()
    }
}

data class LoadingState(
    val percent: Int = 0,
    val status: LoadingStatus = LoadingStatus.Initial,
    val error: String? = null,
)

enum class LoadingStatus {
    Ongoing,
    Complete,
    Initial,
    Error,
}

@HiltViewModel
class MediaLoadingViewModel
@Inject constructor(
    repository: MediaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Map<MediaLoadingType, LoadingState>>(emptyMap())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            MediaLoadingType.entries.forEach { type ->
                repeat(11) { i ->
                    _uiState.update {
                        it.toMutableMap().apply {
                            set(type, LoadingState(i * 10, LoadingStatus.Ongoing))
                        }
                    }
                    if (i == 10) {
                        _uiState.update {
                            it.toMutableMap().apply {
                                set(type, LoadingState(status = LoadingStatus.Complete))
                            }
                        }
                    }
                    delay(200)
                }
            }
        }
    }
}