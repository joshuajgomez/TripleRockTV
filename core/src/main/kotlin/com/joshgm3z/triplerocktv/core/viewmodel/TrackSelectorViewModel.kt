package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.C.FORMAT_HANDLED
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
) {
    override fun toString() = "\n${language}:$label,isSelected=$isSelected"
}

enum class TrackType {
    Subtitle, Audio
}

data class TrackButtonState(
    val enableCaptionsButton: Boolean = false,
    val enableAudioButton: Boolean = false,
)

sealed class ListState {
    class SubtitleTracks(val list: List<TrackInfo>) : ListState()
    class AudioTracks(val list: List<TrackInfo>) : ListState()
    class OnlineSubtitleTracks(val list: List<SubtitleData>) : ListState()
}

data class TrackSelectorUiState(
    val isLoading: Boolean = false,
    val statusText: String = "",
    val listState: ListState? = null,
)

sealed class LoadTrack {
    class OfflineTrack(val trackInfo: TrackInfo) : LoadTrack()
    class OnlineSubtitle(val subtitleData: SubtitleData) : LoadTrack()
}

@HiltViewModel
class TrackSelectorViewModel
@Inject
constructor(
    private val subtitleRepository: SubtitleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrackSelectorUiState?>(TrackSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private var subtitleTracks: List<TrackInfo> = emptyList()
        set(value) {
            field = value
            Logger.debug("subtitleTracks = $value")
        }

    private lateinit var audioTracks: List<TrackInfo>

    private val _trackToLoad = MutableStateFlow<LoadTrack?>(null)
    val trackToLoad = _trackToLoad.asStateFlow()

    private val _trackButtonState = MutableStateFlow(TrackButtonState())
    val trackButtonState = _trackButtonState.asStateFlow()

    init {
        if (BuildConfig.FLAVOR == "demo") {
            subtitleTracks = getDemoTracks(TrackType.Subtitle)
            audioTracks = getDemoTracks(TrackType.Audio)
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

    fun loadTracksOfType(trackType: TrackType) {
        _uiState.update {
            TrackSelectorUiState(
                listState = when (trackType) {
                    TrackType.Subtitle -> ListState.SubtitleTracks(subtitleTracks)
                    else -> ListState.AudioTracks(audioTracks)
                }
            )
        }
    }

    fun onFindMoreClicked(title: String) {
        _uiState.update { it?.copy(isLoading = true) }

        viewModelScope.launch {
            val subtitles = subtitleRepository.findSubtitles(title)
            if (subtitles.isEmpty()) {
                delay(1000)
                _uiState.update {
                    it?.copy(
                        isLoading = false,
                        statusText = "No subtitles found online"
                    )
                }
            } else _uiState.update {
                it?.copy(
                    isLoading = false,
                    listState = ListState.OnlineSubtitleTracks(subtitles)
                )
            }
        }
    }

    fun onTrackClicked(trackInfo: TrackInfo) {
        Logger.debug("trackInfo = [${trackInfo}]")
        if (!trackInfo.isSelected) _trackToLoad.value = LoadTrack.OfflineTrack(trackInfo)
        closeTrackSelectionPopup()
    }

    private fun closeTrackSelectionPopup() {
        viewModelScope.launch {
            _trackToLoad.value = null
            delay(1000)
            _uiState.value = null
        }
    }

    fun onDownloadedSubtitleClick(subtitleData: SubtitleData) {
        Logger.debug("subtitleData.title = [${subtitleData.title}]")
        _uiState.update { it?.copy(isLoading = true) }

        viewModelScope.launch {
            val url = subtitleRepository.getSubtitleUrl(subtitleData.fileId)
            _trackToLoad.value = LoadTrack.OnlineSubtitle(subtitleData.copy(url = url))
        }
    }

    val subtitleTrackListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)

            val subtitleTracks_ = mutableListOf<TrackInfo>()
            val audioTracks_ = mutableListOf<TrackInfo>()

            tracks.groups.forEachIndexed { groupIndex, group ->
                if (group.type.isSubtitleTrack())
                    subtitleTracks_.addAll(group.parseTracks(groupIndex, TrackType.Subtitle))
                if (group.type.isAudioTrack())
                    audioTracks_.addAll(group.parseTracks(groupIndex, TrackType.Audio))
            }

            _trackButtonState.update {
                it.copy(
                    enableCaptionsButton = true,
                    enableAudioButton = audioTracks_.isNotEmpty()
                )
            }
            subtitleTracks = subtitleTracks_.plusDisableSubtitleTrack()
            audioTracks = audioTracks_

            if (trackToLoad.value is LoadTrack.OnlineSubtitle && subtitleTracks.size > 1)
                closeTrackSelectionPopup()
        }
    }
}

private fun MutableList<TrackInfo>.plusDisableSubtitleTrack(): MutableList<TrackInfo> =
    this.apply {
        if (any { it.label == "Disabled" }) return@apply
        add(
            TrackInfo(
                0, 0, "", language = "", label = "Disabled", 0,
                false, !any { it.isSelected }, TrackType.Subtitle
            )
        )
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