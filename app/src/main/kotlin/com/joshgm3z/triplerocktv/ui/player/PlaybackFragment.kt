package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.util.Logger
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackFragment : VideoSupportFragment() {

    private val viewModel: PlaybackViewModel by viewModels()

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private val args by navArgs<PlaybackFragmentArgs>()
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackFragment)

        player = ExoPlayer.Builder(requireActivity()).build()
        player?.addListener(listener)
        val playerAdapter = LeanbackPlayerAdapter(requireActivity(), player!!, 16)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        transportControlGlue = PlaybackTransportControlGlue(requireActivity(), playerAdapter)
        transportControlGlue.host = glueHost

        BackgroundManager.getInstance(requireActivity()).apply {
            drawable = null
        }

        viewModel.fetchStreamDetails(args.streamId, args.browseType)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it.videoUrl.isNotEmpty()) {
                    transportControlGlue.title = it.title
                    transportControlGlue.isSeekEnabled = true
                    transportControlGlue.playWhenPrepared()
                    playVideo(it.videoUrl)
                }
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        Logger.info("videoUrl=[$videoUrl]")
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    val listener = object : Player.Listener {
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
                    player?.seekToDefaultPosition()
                    player?.prepare()
                    return // Try to recover for live streams
                }

                else -> "An unexpected playback error occurred: ${error.localizedMessage}"
            }

            val action = PlaybackFragmentDirections.actionPlaybackFragmentToError(errorMessage)
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
