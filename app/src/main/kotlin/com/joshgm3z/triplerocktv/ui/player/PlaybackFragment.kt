package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    private val viewModel: PlaybackViewModel by viewModels()

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>

    private val args by navArgs<PlaybackFragmentArgs>()

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    private var videoTitle: String? = null

    private val subtitleView: SubtitleView by lazy {
        SubtitleView(requireContext()).apply {
            setUserDefaultStyle()
            setUserDefaultTextSize()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackFragment)

        player.addListener(errorListener(this))
        player.addListener(subtitleListener)

        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage("en") // Or any specific language code
            .build()
        val playerAdapter = LeanbackPlayerAdapter(requireActivity(), player, 16)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        transportControlGlue = createControlGlue(playerAdapter)
        transportControlGlue.host = glueHost

        BackgroundManager.getInstance(requireActivity()).apply {
            drawable = null
        }

        viewModel.fetchStreamDetails(args.streamId, args.browseType)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it.videoUrl.isNotEmpty()) {
                    transportControlGlue.title = it.title
                    videoTitle = it.title
                    transportControlGlue.isSeekEnabled = true
                    transportControlGlue.playWhenPrepared()
                    playVideo(it.videoUrl)
                }
            }
        }
    }

    private fun createControlGlue(
        playerAdapter:
        LeanbackPlayerAdapter
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireActivity(),
            playerAdapter
        ) {
            private val closedCaptioningAction = PlaybackControlsRow.ClosedCaptioningAction(context)

            override fun onCreateSecondaryActions(adapter: ArrayObjectAdapter) {
                adapter.add(closedCaptioningAction)
            }

            override fun onActionClicked(action: Action) {
                if (action === closedCaptioningAction) {
                    videoTitle?.let { title ->
                        findNavController().navigate(
                            PlaybackFragmentDirections
                                .actionPlaybackFragmentToSubtitleFragment(title)
                        )
                    }
                } else {
                    super.onActionClicked(action)
                }
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        Logger.info("videoUrl=[$videoUrl]")
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subtitleView.let { sv ->
            val parent = view as? ViewGroup ?: return

            // Add to view hierarchy
            parent.addView(sv)
        }
    }

    val subtitleListener = object : Player.Listener {
        override fun onCues(cueGroup: androidx.media3.common.text.CueGroup) {
            subtitleView.setCues(cueGroup.cues)
        }
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
