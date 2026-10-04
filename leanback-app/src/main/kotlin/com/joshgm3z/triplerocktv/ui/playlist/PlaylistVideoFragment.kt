package com.joshgm3z.triplerocktv.ui.playlist

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.getValue

@UnstableApi
@AndroidEntryPoint
class PlaylistVideoFragment : VideoSupportFragment() {

    private val playlistViewModel by viewModels<PlaylistViewModel>({ requireParentFragment() })

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    @OptIn(UnstableApi::class)
    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayerFragment()
    }

    @OptIn(UnstableApi::class)
    private fun initPlayerFragment() {
        LeanbackPlayerAdapter(requireContext(), player, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
            transportControlGlue = createControlGlue(this)
        }

        transportControlGlue.host = VideoSupportFragmentGlueHost(this)
        player.playWhenReady = true
    }

    @OptIn(UnstableApi::class)
    fun createControlGlue(
        adapter: LeanbackPlayerAdapter
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireActivity(),
            adapter
        ) {
            override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {}
        }.apply {
            isControlsOverlayAutoHideEnabled = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            playlistViewModel.selectedChannel.debounce(100).collectLatest {
                it?.url?.let { url ->
                    playVideo(url)
                }
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        player.stop()
        player.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun onPause() {
        super.onPause()
        player.stop()
        player.clearMediaItems()
        view?.keepScreenOn = false
    }
}
