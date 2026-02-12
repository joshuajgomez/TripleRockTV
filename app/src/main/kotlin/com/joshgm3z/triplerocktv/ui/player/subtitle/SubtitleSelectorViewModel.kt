package com.joshgm3z.triplerocktv.ui.player.subtitle

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.C.FORMAT_HANDLED
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.joshgm3z.triplerocktv.BuildConfig
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SubtitleInfo(
    val groupIndex: Int,
    val trackIndexInGroup: Int,
    val mimeType: String?,
    val language: String?,
    val label: String?,
    val roleFlags: Int,
    val isSupported: Boolean,
    val isSelected: Boolean,
)

@HiltViewModel
class SubtitleSelectorViewModel
@Inject
constructor() : ViewModel() {

    private val _subtitleUiState = MutableStateFlow<List<SubtitleInfo>?>(null)
    val subtitleUiState = _subtitleUiState.asStateFlow()

    var subtitleToLoad = MutableStateFlow<Any?>(null)

    private var _enableCaptionsButton = MutableStateFlow(false)
    var enableCaptionsButton = _enableCaptionsButton.asStateFlow()

    init {
        if (BuildConfig.FLAVOR == "demo") {
            _subtitleUiState.value = getDemoSubtitles()
        }
    }

    private fun getDemoSubtitles(): List<SubtitleInfo> {
        val list = mutableListOf<SubtitleInfo>()
        repeat(5) {
            list += SubtitleInfo(
                groupIndex = 0,
                trackIndexInGroup = 0,
                mimeType = MimeTypes.APPLICATION_MP4,
                language = "en",
                label = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                roleFlags = 0,
                isSupported = true,
                isSelected = it == 1
            )
        }
        return list
    }

    fun onSubtitleClicked(subtitleInfo: SubtitleInfo) {
        Logger.debug("subtitleInfo = [${subtitleInfo}]")
        subtitleToLoad.value = subtitleInfo
    }

    val subtitleTrackListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            val subtitleTracks = listSubtitleTracks(tracks)
            Logger.debug("subtitleTracks = [$subtitleTracks]")
            _enableCaptionsButton.value = subtitleTracks.isNotEmpty()
            _subtitleUiState.value = subtitleTracks
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
                        FORMAT_HANDLED -> true
                        else -> false // unsupported/supported values differ between ExoPlayer versions — adjust if necessary
                    }

                    result += SubtitleInfo(
                        groupIndex = groupIndex,
                        trackIndexInGroup = i,
                        mimeType = mime,
                        language = language,
                        label = label,
                        roleFlags = roleFlags,
                        isSupported = isSupported,
                        isSelected = group.isTrackSelected(i)
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
