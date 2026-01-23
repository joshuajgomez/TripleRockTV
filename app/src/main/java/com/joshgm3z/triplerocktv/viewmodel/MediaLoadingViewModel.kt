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

data class MediaLoadingUiState(
    var videoLoadingState: LoadingState = LoadingState(),
    var seriesLoadingState: LoadingState = LoadingState(),
    var liveTvLoadingState: LoadingState = LoadingState(),
    var parsingState: LoadingState = LoadingState(),
) {
    companion object {
        fun sample() = MediaLoadingUiState(
            videoLoadingState = LoadingState(
                percent = 50,
                status = LoadingStatus.Complete,
            ),
            seriesLoadingState = LoadingState(
                percent = 50,
                status = LoadingStatus.Ongoing,
            ),
            liveTvLoadingState = LoadingState(
                percent = 50,
                status = LoadingStatus.Error,
            ),
            parsingState = LoadingState(
                percent = 50,
                status = LoadingStatus.Initial,
            )
        )
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
    private val _uiState = MutableStateFlow(MediaLoadingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repeat(11) { i ->
                _uiState.update {
                    it.copy(videoLoadingState = LoadingState(10 * i, LoadingStatus.Ongoing))
                }
                if (i == 10) {
                    _uiState.update {
                        it.copy(videoLoadingState = LoadingState(status = LoadingStatus.Complete))
                    }
                }
                delay(200)
            }
            repeat(11) { i ->
                _uiState.update {
                    it.copy(seriesLoadingState = LoadingState(10 * i, LoadingStatus.Ongoing))
                }
                if (i == 10) {
                    _uiState.update {
                        it.copy(seriesLoadingState = LoadingState(status = LoadingStatus.Complete))
                    }
                }
                delay(200)
            }
            repeat(11) { i ->
                _uiState.update {
                    it.copy(liveTvLoadingState = LoadingState(10 * i, LoadingStatus.Ongoing))
                }
                if (i == 10) {
                    _uiState.update {
                        it.copy(liveTvLoadingState = LoadingState(status = LoadingStatus.Complete))
                    }
                }
                delay(200)
            }
            repeat(11) { i ->
                _uiState.update {
                    it.copy(parsingState = LoadingState(10 * i, LoadingStatus.Ongoing))
                }
                if (i == 10) {
                    _uiState.update {
                        it.copy(parsingState = LoadingState(status = LoadingStatus.Complete))
                    }
                }
                delay(200)
            }
        }
    }
}