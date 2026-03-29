package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
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
    val playbackItem: Any,
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: MediaLocalRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _playbackUiState = MutableStateFlow<PlaybackUiState?>(null)
    val playbackUiState = _playbackUiState.asStateFlow()

    private var streamId: Int? = null
    private var seriesId: Int? = null
    private var streamType: StreamType? = null

    fun fetchStreamDetails(streamId: Int, streamType: StreamType, seriesId: Int? = null) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        this.streamType = streamType
        this.seriesId = seriesId
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            this@PlaybackViewModel.streamId = streamId
            when (streamType) {
                StreamType.VideoOnDemand -> _playbackUiState.update {
                    repository.updateLastPlayedTimestamp(streamId, StreamType.VideoOnDemand)
                    val result = repository.fetchStream(streamId, streamType)
                    PlaybackUiState(
                        playbackItem = result,
                        videoUrl = result.videoUrl(userInfo),
                    )
                }

                StreamType.Series -> _playbackUiState.update {
                    repository.updateEpisodeLastPlayedTimestamp(streamId, seriesId!!)
                    val episode = repository.fetchEpisode(streamId, seriesId)
                    PlaybackUiState(
                        playbackItem = episode!!,
                        videoUrl = episode.videoUrl(userInfo),
                    )
                }

                else -> return@launch
            }
        }
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                when (streamType) {
                    StreamType.VideoOnDemand -> repository.updatePlayedDuration(
                        it,
                        positionMs,
                        streamType!!
                    )

                    StreamType.Series -> repository.updateEpisodePlayedDuration(
                        it,
                        seriesId!!,
                        positionMs
                    )

                    else -> return@launch
                }
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
