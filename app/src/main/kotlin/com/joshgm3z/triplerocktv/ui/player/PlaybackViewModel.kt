package com.joshgm3z.triplerocktv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
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
    val videoUrl: String,
    val streamData: StreamData,
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: MediaLocalRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _playbackUiState = MutableStateFlow<PlaybackUiState?>(null)
    val playbackUiState = _playbackUiState.asStateFlow()

    private var streamId: Int? = null
    private var streamType: StreamType? = null

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        this.streamType = streamType
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            this@PlaybackViewModel.streamId = streamId
            when (val result = repository.fetchStream(streamId, streamType)) {
                is StreamData -> _playbackUiState.update {
                    repository.updateLastPlayedTimestamp(streamId, StreamType.VideoOnDemand)
                    PlaybackUiState(
                        streamData = result,
                        videoUrl = result.videoUrl(userInfo),
                    )
                }

                is Episode -> _playbackUiState.update {
                    repository.updateLastPlayedTimestamp(streamId, StreamType.Series)
                    PlaybackUiState(
                        streamData = StreamData.sample(),
                        videoUrl = result.videoUrl(userInfo),
                    )
                }
            }
        }
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updatePlayedDuration(it, positionMs, streamType!!)
            }
        } ?: throw Exception("Stream id is null")
    }

    fun updateSelectedSubtitle(language: String, title: String, url: String?) {
        Logger.debug("url = [${url}], language = [${language}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateSelectedSubtitle(it, language, title, url)
            }
        } ?: throw Exception("Stream id is null")
    }
}
