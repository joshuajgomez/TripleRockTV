package com.joshgm3z.triplerocktv.ui.player

import androidx.annotation.OptIn
import androidx.media3.common.C.TRACK_TYPE_AUDIO
import androidx.media3.common.C.TRACK_TYPE_TEXT
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.joshgm3z.triplerocktv.util.Logger

@OptIn(UnstableApi::class)
fun ExoPlayer.playerListener(
    fragment: PlaybackFragment,
    onSubtitleTrackFound: () -> Unit,
    onAudioTracksFound: () -> Unit,
) = object : Player.Listener {
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        handleError(fragment, error)
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        Logger.debug("tracks = [${tracks}]")
        if (tracks.containsType(TRACK_TYPE_TEXT)) {
            Logger.debug("subtitles found")
            onSubtitleTrackFound()
        }
        if (tracks.containsType(TRACK_TYPE_AUDIO)) {
            Logger.debug("audio tracks found")
            onAudioTracksFound()
        }
    }
}

@OptIn(UnstableApi::class)
fun ExoPlayer.handleError(
    fragment: PlaybackFragment,
    error: PlaybackException
) {
    val errorMessage = when (error.errorCode) {
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
            "Network error: Please check your internet connection."

        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
        PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED ->
            "Video format not supported on this device."

        PlaybackException.ERROR_CODE_REMOTE_ERROR ->
            "Server error: Could not reach the video stream."

        PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
            seekToDefaultPosition()
            prepare()
            return // Try to recover for live streams
        }

        else -> "An unexpected playback error occurred: ${error.localizedMessage}"
    }

    val action = PlaybackFragmentDirections.actionPlaybackFragmentToError(errorMessage)
    findNavController(fragment).navigate(action)
}