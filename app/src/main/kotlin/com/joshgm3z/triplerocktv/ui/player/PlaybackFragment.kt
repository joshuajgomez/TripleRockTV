package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C.SELECTION_FLAG_DEFAULT
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.media3.common.text.CueGroup
import com.joshgm3z.triplerocktv.ui.player.track.TrackInfo
import com.joshgm3z.triplerocktv.ui.player.track.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.ui.player.track.TrackType

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackFragment : VideoSupportFragment() {

    private val viewModel: PlaybackViewModel by viewModels()
    private val trackViewModel: TrackSelectorViewModel by hiltNavGraphViewModels(
        R.id.nav_graph
    )

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
        player.addListener(playbackListener)
        player.addListener(trackViewModel.subtitleTrackListener)

        LeanbackPlayerAdapter(requireContext(), player, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
            transportControlGlue = createControlGlue(this)
        }

        transportControlGlue.host = glueHost

        BackgroundManager.getInstance(requireActivity()).apply {
            drawable = null
        }

        viewModel.fetchStreamDetails(args.streamId, args.streamType)
        lifecycleScope.launch {
            viewModel.playbackUiState.collectLatest {
                if (it.videoUrl.isNotEmpty()) {
                    transportControlGlue.title = it.title
                    videoTitle = it.title
                    transportControlGlue.isSeekEnabled = true
                    transportControlGlue.playWhenPrepared()
                    playVideo(it)
                }
            }
        }

        lifecycleScope.launch {
            trackViewModel.subtitleTrackToLoad.collectLatest { it ->
                Logger.debug("subtitleTrackToLoad $it")
                it?.let {
                    trackViewModel.subtitleTrackToLoad.value = null
                    var subtitleUrl: String? = null
                    var subtitleLanguage = ""
                    var subtitleTitle = ""
                    when (it) {
                        is SubtitleData -> {
                            subtitleLanguage = it.language!!
                            subtitleUrl = it.url
                            subtitleTitle = it.title
                            loadSubtitle(it)
                        }

                        is TrackInfo -> {
                            subtitleLanguage = it.language!!
                            subtitleTitle = it.label!!
                            switchTrack(it)
                        }

                        else -> Logger.warn("Unknown track type: ${it::class.java}")
                    }
                    viewModel.updateSelectedSubtitle(subtitleLanguage, subtitleTitle, subtitleUrl)
                }
            }
        }
        lifecycleScope.launch {
            trackViewModel.audioTrackToLoad.collectLatest { it ->
                Logger.debug("audioTrackToLoad $it")
                it?.let {
                    trackViewModel.audioTrackToLoad.value = null
                    switchTrack(it)
                }
            }
        }

        lifecycleScope.launch {
            trackViewModel.trackButtonState.collectLatest {
                Logger.debug("trackButtonState $it")
            }
        }
    }

    private fun switchTrack(trackInfo: TrackInfo) {
        val currentMediaItem = player.currentMediaItem ?: return
        val currentPosition = player.currentPosition
        val playWhenReady = player.playWhenReady

        val param = player.trackSelectionParameters.buildUpon()

        player.trackSelectionParameters = when (trackInfo.trackType) {
            TrackType.Subtitle -> param.setPreferredTextLanguage(trackInfo.language)
            TrackType.Audio -> param.setPreferredAudioLanguage(trackInfo.language)
        }.build()

        player.setMediaItem(currentMediaItem, false)
        player.prepare()
        player.seekTo(currentPosition)
        player.playWhenReady = playWhenReady
    }

    private fun loadSubtitle(subtitleData: SubtitleData) {
        Logger.debug("subtitleData = [${subtitleData}]")
        val currentMediaItem = player.currentMediaItem ?: return
        val currentPosition = player.currentPosition
        val playWhenReady = player.playWhenReady

        // 1. Create the subtitle configuration
        subtitleData.url ?: return
        val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(subtitleData.url.toUri())
            .setMimeType("application/x-subrip")
            .setLanguage(subtitleData.language)
            .setLabel(subtitleData.title)
            .setSelectionFlags(SELECTION_FLAG_DEFAULT)
            .build()
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage(subtitleData.language) // Or any specific language code
            .build()
        // 2. Rebuild the MediaItem with the new subtitle
        val updatedMediaItem = currentMediaItem.buildUpon()
            .setSubtitleConfigurations(listOf(subtitleConfig))
            .build()

        // 3. Update the player
        player.setMediaItem(
            updatedMediaItem,
            false
        ) // false means don't reset position, but seek is safer
        player.prepare()
        player.seekTo(currentPosition)
        player.playWhenReady = playWhenReady

        Logger.info("Subtitle loaded from: ${subtitleData.url}")
    }

    private fun createControlGlue(
        playerAdapter:
        LeanbackPlayerAdapter
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireActivity(),
            playerAdapter
        ) {
            private val closedCaptioningAction =
                PlaybackControlsRow.ClosedCaptioningAction(context).apply {
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_subtitles)
                }
            private val audioAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_voice)
            }

            override fun onCreateSecondaryActions(adapter: ArrayObjectAdapter) {
                adapter.add(closedCaptioningAction)
                adapter.add(audioAction)
            }

            override fun onActionClicked(action: Action) {
                when {
                    action === closedCaptioningAction -> {
                        videoTitle?.let { title ->
                            val action = PlaybackFragmentDirections.toTrackSelector()
                            action.title = title
                            action.trackType = TrackType.Subtitle
                            findNavController().navigate(action)
                        }
                    }

                    action === audioAction -> {
                        val action = PlaybackFragmentDirections.toTrackSelector()
                        action.trackType = TrackType.Audio
                        findNavController().navigate(action)
                    }

                    else -> {
                        super.onActionClicked(action)
                    }
                }
            }
        }
    }

    private fun playVideo(uiState: PlaybackUiState) {
        Logger.info("videoUrl=[$uiState]")
        val mediaItem = MediaItem.Builder()
            .setUri(uiState.videoUrl)
            .build()

        uiState.subtitleLanguage?.let {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setPreferredTextLanguage(it)
                .build()
        }
        uiState.subtitleUrl?.let {
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(it.toUri())
                .setMimeType("application/x-subrip")
                .setLanguage(uiState.subtitleLanguage)
                .setLabel(uiState.subtitleTitle)
                .setSelectionFlags(SELECTION_FLAG_DEFAULT)
                .build()
            mediaItem.buildUpon()
                .setSubtitleConfigurations(listOf(subtitleConfig))
                .build()
        }

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

    val playbackListener = object : Player.Listener {
        override fun onCues(cueGroup: CueGroup) {
            subtitleView.setCues(cueGroup.cues)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                val durationMs = player.duration
                if (durationMs > 0) {
                    Logger.info("Total video duration: $durationMs ms")
                    viewModel.updateTotalDuration(durationMs)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateLastPlayedPosition(player.currentPosition)
        transportControlGlue.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
