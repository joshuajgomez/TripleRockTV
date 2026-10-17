package com.joshgm3z.triplerocktv.core.util

import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
fun errorListener(
    seekToDefaultPosition: () -> Unit = {},
    prepare: () -> Unit = {},
    onError: (title: String, message: String) -> Unit = { _, _ -> },
) = object : Player.Listener {
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        val errorMessage = when (error.errorCode) {
            // 1. Network & Connectivity Issues
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Please check your internet connection"

            // 2. Resource/Server Issues
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
            PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> "Could not reach the video stream"

            // 3. Format & Decoding Issues
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES -> "Video format is not supported on this device"

            // 4. Content Parsing Issues
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
            PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED,
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "The video stream is corrupted or invalid"

            // 5. Live Stream Recovery
            PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                Logger.info("Behind live window, attempting recovery...")
                seekToDefaultPosition()
                prepare()
                return
            }

            // 6. Security/DRM Issues (Optional, but recommended)
            PlaybackException.ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED,
            PlaybackException.ERROR_CODE_DRM_PROVISIONING_FAILED -> "Cannot acquire playback rights"

            // 7. Cleartext issues (Common when trying to play HTTP on modern Android)
            PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED -> "Non-secure (HTTP) playback is not permitted"

            else -> error.localizedMessage ?: "Unknown error"
        }

        Logger.error(errorMessage)
        onError(errorMessage, "Error code ${error.errorCode}")
    }
}
