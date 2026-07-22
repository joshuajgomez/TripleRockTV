package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.util.Logger
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
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val recentsRepository: RecentsRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _playbackUiState = MutableStateFlow<PlaybackUiState?>(null)
    val playbackUiState = _playbackUiState.asStateFlow()

    val streamId = savedStateHandle.get<Int>("streamId")
        ?: throw Exception("Missing nav arg streamId")
    private val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw Exception("Missing nav arg streamType")
    private val seriesId = savedStateHandle.get<Int>("seriesId")

    init {
        if (streamType == StreamType.Series && seriesId == null) {
            throw Exception("Missing nav arg seriesId for series stream")
        }
        fetchStreamDetails(streamId, streamType, seriesId)
    }

    fun fetchStreamDetails(streamId: Int, streamType: StreamType, seriesId: Int? = null) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            when (streamType) {
                StreamType.VideoOnDemand -> _playbackUiState.update {
                    val result = repository.fetchStream(streamId, streamType)
                    PlaybackUiState(
                        playbackItem = result,
                        videoUrl = result.videoUrl(userInfo),
                    )
                }

                StreamType.LiveTV -> _playbackUiState.update {
                    val result = repository.fetchStream(streamId, streamType)
                    PlaybackUiState(
                        playbackItem = result,
                        videoUrl = result.videoUrl(userInfo),
                    )
                }

                StreamType.Series -> _playbackUiState.update {
                    val episode = repository.fetchEpisode(streamId, seriesId!!)
                    PlaybackUiState(
                        playbackItem = episode!!,
                        videoUrl = episode.videoUrl(userInfo),
                    )
                }
            }
        }
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                when (streamType) {
                    StreamType.VideoOnDemand -> recentsRepository.updatePlayedDuration(
                        streamId = it,
                        positionMs = positionMs,
                        streamType = streamType
                    )

                    StreamType.LiveTV -> recentsRepository.updatePlayedDuration(
                        streamId = it,
                        streamType = streamType
                    )

                    StreamType.Series -> recentsRepository.updatePlayedDuration(
                        streamId = it,
                        seriesId = seriesId,
                        positionMs = positionMs,
                        streamType = streamType,
                    )
                }
            }
        } ?: throw Exception("Stream id is null")
    }

    fun updateSelectedSubtitle(language: String, title: String, url: String?) {
        Logger.debug("url = [${url}], language = [${language}]")
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedSubtitle(streamId, language, title, url)
        }
    }
}