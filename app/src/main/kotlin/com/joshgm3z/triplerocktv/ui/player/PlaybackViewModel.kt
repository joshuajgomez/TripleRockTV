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
    val subtitleUrl: String? = null,
    val subtitleLanguage: String? = null,
    val subtitleTitle: String? = null,
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: MediaLocalRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _playbackUiState = MutableStateFlow(PlaybackUiState())
    val playbackUiState = _playbackUiState.asStateFlow()

    private var streamId: Int? = null

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            this@PlaybackViewModel.streamId = streamId
            when (val result = repository.fetchStream(streamId, streamType)) {
                is StreamData -> _playbackUiState.update {
                    repository.updateLastPlayedTimestamp(streamId, System.currentTimeMillis())
                    it.copy(
                        title = result.name,
                        videoUrl = result.videoUrl(userInfo),
                        subtitleUrl = result.subtitleUrl,
                        subtitleLanguage = result.subtitleLanguage,
                        subtitleTitle = result.subtitleUrl,
                    )
                }

                is SeriesStream -> _playbackUiState.update {
                    it.copy(
                        title = result.name,
                        videoUrl = result.videoUrl(userInfo)
                    )
                }
            }
        }
    }

    fun updateTotalDuration(durationMs: Long) {
        Logger.debug("durationMs = [${durationMs}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateTotalDuration(it, durationMs)
            }
        } ?: throw Exception("Stream id is null")
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updatePlayedDuration(it, positionMs)
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
