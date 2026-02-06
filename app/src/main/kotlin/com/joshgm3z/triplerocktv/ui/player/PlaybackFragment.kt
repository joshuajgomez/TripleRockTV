package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.ui.details.DetailsViewModel
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackFragment : VideoSupportFragment() {

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private val args by navArgs<PlaybackFragmentArgs>()
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackFragment)

        player = ExoPlayer.Builder(requireActivity()).build()
        val playerAdapter = LeanbackPlayerAdapter(requireActivity(), player!!, 16)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        transportControlGlue = PlaybackTransportControlGlue(requireActivity(), playerAdapter)
        transportControlGlue.host = glueHost

        viewModel.fetchStreamDetails(args.streamId)
        lifecycleScope.launch {
            viewModel.stream.collectLatest {
                it?.let {
                    transportControlGlue.title = it.name
                    transportControlGlue.playWhenPrepared()
                    playVideo(it.streamId)
                }
            }
        }
    }

    private fun playVideo(streamId: Int) {
        val videoUrl =
            "${Secrets.webUrl}/movie/${Secrets.username}/${Secrets.password}/$streamId.mkv"
        Logger.info("videoUrl=[$videoUrl]")
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()
        player?.setMediaItem(mediaItem)
        player?.prepare()
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
