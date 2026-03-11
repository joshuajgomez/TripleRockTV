package com.joshgm3z.triplerocktv.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

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

    var streamData: StreamData? = null

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        Logger.debug("streamId = [${streamId}], browseType = [${streamType}]")
        viewModelScope.launch {
            repository.streamDataFlow(streamId, streamType).collectLatest {
                streamData = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            when (val result = repository.fetchStream(streamId, streamType)) {
                is StreamData -> _playbackUiState.update {
                    repository.updateLastPlayedTimestamp(streamId)
                    PlaybackUiState(
                        streamData = result,
                        videoUrl = result.videoUrl(userInfo),
                    )
                }
            }
        }
    }

    fun updateTotalDuration(durationMs: Long) {
        Logger.debug("durationMs = [${durationMs}]")
        streamData?.streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateTotalDuration(it, durationMs)
            }
        } ?: throw Exception("Stream id is null")
    }

    fun updateLastPlayedPosition(positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        streamData?.streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updatePlayedDuration(it, positionMs)
            }
        } ?: throw Exception("Stream id is null")
    }

    fun updateSelectedSubtitle(language: String, title: String, url: String?) {
        Logger.debug("url = [${url}], language = [${language}]")
        streamData?.streamId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateSelectedSubtitle(it, language, title, url)
            }
        } ?: throw Exception("Stream id is null")
    }

    @SuppressLint("RestrictedApi")
    fun updateContinueWatching(context: Context, positionMs: Long) {
        Logger.debug("positionMs = [${positionMs}]")
        val builder = WatchNextProgram.Builder()
            .setType(TvContractCompat.PreviewPrograms.TYPE_MOVIE) // Use TYPE_TV_EPISODE for shows
            .setWatchNextType(TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE)
            .setLastEngagementTimeUtcMillis(System.currentTimeMillis())
            .setLastPlaybackPositionMillis(positionMs.toInt())
            .setDurationMillis(streamData?.totalDuration?.toInt() ?: 0)
            .setTitle(streamData?.name)
            .setPosterArtUri(Uri.parse(streamData?.streamIcon))
            .setInternalProviderId(streamData?.streamId.toString())
            // Crucial: This URI must be what your app handles in its Manifest to deep-link to playback
            .setIntentUri("triplerock://video/${streamData?.streamId}".toUri())

        val programUri = context.contentResolver.insert(
            TvContractCompat.WatchNextPrograms.CONTENT_URI,
            builder.build().toContentValues()
        )
    }
}
