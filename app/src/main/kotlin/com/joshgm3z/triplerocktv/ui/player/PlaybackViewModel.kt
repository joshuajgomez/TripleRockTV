package com.joshgm3z.triplerocktv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaybackUiState(
    val title: String = "",
    val videoUrl: String = "",
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: MediaLocalRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState = _uiState.asStateFlow()

    private var streamId: Int? = null

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            this@PlaybackViewModel.streamId = streamId
            when (val result = repository.fetchStream(streamId, streamType)) {
                is StreamData -> _uiState.update {
                    it.copy(
                        title = result.name,
                        videoUrl = result.videoUrl(userInfo)
                    )
                }

                is SeriesStream -> _uiState.update {
                    it.copy(
                        title = result.name,
                        videoUrl = result.videoUrl(userInfo)
                    )
                }
            }
        }
    }

    fun updateTotalDuration(durationMs: Long) {
        streamId?.let {
            repository.updateTotalDuration(it, durationMs)
        } ?: throw Exception("Stream id is null")
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        streamId?.let {
            repository.updateLastPlayedPosition(it, positionMs)
        } ?: throw Exception("Stream id is null")
    }
}
