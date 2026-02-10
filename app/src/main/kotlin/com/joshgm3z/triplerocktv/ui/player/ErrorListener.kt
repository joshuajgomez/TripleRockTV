package com.joshgm3z.triplerocktv.ui.player

import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

@OptIn(UnstableApi::class)
fun errorListener(
    fragment: PlaybackFragment,
    seekToDefaultPosition: () -> Unit = {},
    prepare: () -> Unit = {}
) = object : Player.Listener {
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

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
}
