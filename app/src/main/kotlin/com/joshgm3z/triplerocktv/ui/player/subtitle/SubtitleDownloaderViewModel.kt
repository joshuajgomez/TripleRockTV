package com.joshgm3z.triplerocktv.ui.player.subtitle

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.ui.player.SubtitleInfo
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubtitleUiState(
    val currentSubtitle: SubtitleData? = null,
    val defaultSubtitleList: List<SubtitleData>? = null,
    val downloadedSubtitleList: List<SubtitleData>? = null,
)

@HiltViewModel
class SubtitleDownloaderViewModel
@Inject
constructor(
    private val subtitleRepository: SubtitleRepository
) : ViewModel() {

    private val _subtitleUiState = MutableStateFlow(SubtitleUiState())
    val subtitleUiState = _subtitleUiState.asStateFlow()

    fun onFindClicked(query: String) {
        viewModelScope.launch {
            _subtitleUiState.update {
                it.copy(downloadedSubtitleList = subtitleRepository.findSubtitles(query))
            }
        }
    }

    fun onSubtitleClicked(subtitleData: SubtitleData) {
        Logger.debug("subtitleData = [${subtitleData}]")
        viewModelScope.launch {
            val url = subtitleRepository.getSubtitleUrl(subtitleData.fileId)
            Logger.debug("url = [${url}]")
            _subtitleUiState.update {
                it.copy(
                    currentSubtitle = subtitleData.copy(url = url),
                    defaultSubtitleList = listOf(subtitleData.copy(url = url))
                )
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
