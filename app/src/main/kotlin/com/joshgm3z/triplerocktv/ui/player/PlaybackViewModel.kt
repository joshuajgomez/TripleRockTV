package com.joshgm3z.triplerocktv.ui.player

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
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

data class SubtitleInfo(
    val groupIndex: Int,
    val trackIndexInGroup: Int,
    val mimeType: String?,
    val language: String?,
    val label: String?,
    val roleFlags: Int,
    val isSupported: Boolean
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: MediaLocalRepository,
    private val localDataStore: LocalDatastore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchStreamDetails(streamId: Int, browseType: BrowseType) {
        Logger.debug("streamId = [${streamId}], browseType = [${browseType}]")
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = localDataStore.getUserInfo()!!
            when (val result = repository.fetchStream(streamId, browseType)) {
                is VodStream -> _uiState.update {
                    it.copy(
                        title = result.name,
                        videoUrl = result.videoUrl(userInfo)
                    )
                }

                is LiveTvStream -> _uiState.update {
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

    val subtitleTrackListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            val subtitleTracks = listSubtitleTracks(tracks)
            Logger.debug("subtitleTracks = [$subtitleTracks]")
        }
    }

    @OptIn(UnstableApi::class)
    fun listSubtitleTracks(tracks: Tracks): List<SubtitleInfo> {
        val result = mutableListOf<SubtitleInfo>()
        tracks.groups.forEachIndexed { groupIndex, group ->
            if (group.type == C.TRACK_TYPE_TEXT) {
                // group.trackCount is the number of tracks in this group
                for (i in 0 until group.length) {
                    val format: Format = group.getTrackFormat(i)
                    // Format fields commonly available: sampleMimeType, language, label, roleFlags
                    val mime = format.sampleMimeType
                    val language = format.language
                    val label = format.label
                    val roleFlags = format.roleFlags
                    // group.isTrackSupported(i) returns an Int support level in Media3 (>= C.FORMAT_HANDLED means supported)
                    val isSupported = when (group.getTrackSupport(i)) {
                        1 -> false
                        else -> true // unsupported/supported values differ between ExoPlayer versions — adjust if necessary
                    }

                    result += SubtitleInfo(
                        groupIndex = groupIndex,
                        trackIndexInGroup = i,
                        mimeType = mime,
                        language = language,
                        label = label,
                        roleFlags = roleFlags,
                        isSupported = isSupported
                    )

                    Logger.debug(
                        "Subtitle found: group=$groupIndex track=$i mime=$mime lang=$language label=$label roleFlags=$roleFlags supported=$isSupported"
                    )
                }
            }
        }
        return result
    }
}
