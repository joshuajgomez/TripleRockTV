package com.joshgm3z.triplerocktv.ui.player.track

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
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TrackInfo(
    val groupIndex: Int,
    val trackIndexInGroup: Int,
    val mimeType: String?,
    val language: String?,
    val label: String?,
    val roleFlags: Int,
    val isSupported: Boolean,
    val isSelected: Boolean,
    val trackType: TrackType,
)

enum class TrackType {
    Subtitle, Audio
}

data class TrackButtonState(
    val enableCaptionsButton: Boolean = false,
    val enableAudioButton: Boolean = false,
)

@HiltViewModel
class TrackSelectorViewModel
@Inject
constructor() : ViewModel() {

    private val _subtitleTrackListState = MutableStateFlow<List<TrackInfo>?>(null)
    val subtitleTrackListState = _subtitleTrackListState.asStateFlow()

    private val _audioTrackListState = MutableStateFlow<List<TrackInfo>?>(null)
    val audioTrackListState = _audioTrackListState.asStateFlow()

    var subtitleTrackToLoad = MutableStateFlow<Any?>(null)

    var audioTrackToLoad = MutableStateFlow<TrackInfo?>(null)

    private val _trackButtonState = MutableStateFlow(TrackButtonState())
    val trackButtonState = _trackButtonState.asStateFlow()

    init {
        if (BuildConfig.FLAVOR == "demo") {
            _subtitleTrackListState.value = getDemoTracks(TrackType.Subtitle)
            _audioTrackListState.value = getDemoTracks(TrackType.Audio)
        }
    }

    private fun getDemoTracks(trackType: TrackType): List<TrackInfo> {
        val list = mutableListOf<TrackInfo>()
        repeat(5) {
            list += TrackInfo(
                groupIndex = 0,
                trackIndexInGroup = 0,
                mimeType = MimeTypes.APPLICATION_MP4,
                language = "en",
                label = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                roleFlags = 0,
                isSupported = true,
                isSelected = it == 1,
                trackType = trackType
            )
        }
        return list
    }

    fun onTrackClicked(trackInfo: TrackInfo) {
        Logger.debug("trackInfo = [${trackInfo}]")
        when (trackInfo.trackType) {
            TrackType.Subtitle -> subtitleTrackToLoad.value = trackInfo
            else -> audioTrackToLoad.value = trackInfo
        }
    }

    val subtitleTrackListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)

            val subtitleTracks = mutableListOf<TrackInfo>()
            val audioTracks = mutableListOf<TrackInfo>()

            tracks.groups.forEachIndexed { groupIndex, group ->
                if (group.type.isSubtitleTrack())
                    subtitleTracks.addAll(group.parseTracks(groupIndex, TrackType.Subtitle))
                if (group.type.isAudioTrack())
                    audioTracks.addAll(group.parseTracks(groupIndex, TrackType.Audio))
            }

            _trackButtonState.update {
                it.copy(
                    enableCaptionsButton = subtitleTracks.isNotEmpty(),
                    enableAudioButton = audioTracks.isNotEmpty()
                )
            }
            _subtitleTrackListState.value = subtitleTracks
            _audioTrackListState.value = audioTracks
        }
    }

}

@OptIn(UnstableApi::class)
private fun Tracks.Group.parseTracks(
    groupIndex: Int,
    trackType: TrackType,
): List<TrackInfo> {
    val tracks = mutableListOf<TrackInfo>()
    for (i in 0 until length) {
        val format: Format = getTrackFormat(i)
        tracks += TrackInfo(
            groupIndex = groupIndex,
            trackIndexInGroup = i,
            mimeType = format.sampleMimeType,
            language = format.language,
            label = format.label,
            roleFlags = format.roleFlags,
            isSupported = FORMAT_HANDLED == getTrackSupport(i),
            isSelected = isTrackSelected(i),
            trackType = trackType
        ).apply {
            Logger.debug("TrackType$trackType=$this")
        }
    }
    return tracks
}

fun Int.isSubtitleTrack() = this == C.TRACK_TYPE_TEXT
fun Int.isAudioTrack() = this == C.TRACK_TYPE_AUDIO