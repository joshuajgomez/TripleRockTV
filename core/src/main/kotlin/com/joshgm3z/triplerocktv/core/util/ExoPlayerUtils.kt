package com.joshgm3z.triplerocktv.core.util

import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.C.SELECTION_FLAG_DEFAULT
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.core.viewmodel.TrackType

fun Player.switchTrack(trackInfo: TrackInfo) {
    Logger.debug("trackInfo = [${trackInfo}]")
    val parametersBuilder = trackSelectionParameters.buildUpon()

    when (trackInfo.trackType) {
        TrackType.Subtitle -> {
            if (trackInfo.disableTrack) {
                // Completely disable text tracks
                parametersBuilder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            } else {
                trackInfo.trackGroup?.let {
                    val override = TrackSelectionOverride(
                        it,
                        listOf(trackInfo.trackIndexInGroup)
                    )
                    parametersBuilder
                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                        .setOverrideForType(override)
                }
            }
        }

        TrackType.Audio -> {
            parametersBuilder.setPreferredAudioLanguage(trackInfo.language)
        }
    }

    trackSelectionParameters = parametersBuilder.build()
}

fun Player.loadSubtitle(subtitleData: SubtitleData) {
    Logger.debug("subtitleData = [${subtitleData}]")
    val currentMediaItem = currentMediaItem ?: return
    val currentPosition = currentPosition
    val playWhenReady = playWhenReady

    // 1. Create the subtitle configuration
    subtitleData.url ?: return
    val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(subtitleData.url.toUri())
        .setMimeType("application/x-subrip")
        .setLanguage(subtitleData.language)
        .setId("online")
        .setLabel(subtitleData.title)
        .setSelectionFlags(SELECTION_FLAG_DEFAULT)
        .build()
    trackSelectionParameters = trackSelectionParameters
        .buildUpon()
        .setPreferredTextLanguage(subtitleData.language) // Or any specific language code
        .build()
    // 2. Rebuild the MediaItem with the new subtitle
    val updatedMediaItem = currentMediaItem.buildUpon()
        .setSubtitleConfigurations(listOf(subtitleConfig))
        .build()

    // 3. Update the player
    setMediaItem(
        updatedMediaItem,
        false
    ) // false means don't reset position, but seek is safer
    prepare()
    seekTo(currentPosition)
    this.playWhenReady = playWhenReady

    Logger.info("Subtitle loaded from: ${subtitleData.url}")
}