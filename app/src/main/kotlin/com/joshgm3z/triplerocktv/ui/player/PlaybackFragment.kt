package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import com.joshgm3z.triplerocktv.util.setBackground

const val FAST_FORWARD_DURATION = 10000

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

        requireActivity().setBackground(null)

        viewModel.fetchStreamDetails(args.streamId, args.streamType)
        lifecycleScope.launch {
            viewModel.playbackUiState.collectLatest {
                it?.let { playbackUiState ->
                    transportControlGlue.title = playbackUiState.streamData.name
                    playbackUiState.streamData.movieMetadata?.genre?.let { subtitle ->
                        transportControlGlue.subtitle = subtitle
                    }
                    videoTitle = playbackUiState.streamData.name
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

            private val fastForwardAction = PlaybackControlsRow.FastForwardAction(context)
            private val rewindAction = PlaybackControlsRow.RewindAction(context)

            override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
                adapter.add(rewindAction)
                super.onCreatePrimaryActions(adapter)
                adapter.add(fastForwardAction)
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

                    action === rewindAction -> {
                        val newPos =
                            (player.currentPosition - FAST_FORWARD_DURATION).coerceAtLeast(0)
                        player.seekTo(newPos)
                    }

                    // 5. Handle Fast Forward (seek forward 10 seconds)
                    action === fastForwardAction -> {
                        val newPos =
                            (player.currentPosition + FAST_FORWARD_DURATION).coerceAtMost(player.duration)
                        player.seekTo(newPos)
                    }

                    else -> {
                        super.onActionClicked(action)
                    }
                }
            }
        }
    }

    private fun playVideo(uiState: PlaybackUiState) {
        Logger.info("uiState=[$uiState]")
        var mediaItem = MediaItem.Builder()
            .setUri(uiState.videoUrl)
            .build()

        uiState.streamData.subtitleLanguage?.let {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setPreferredTextLanguage(it)
                .build()
        }
        uiState.streamData.subtitleUrl?.let {
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(it.toUri())
                .setMimeType("application/x-subrip")
                .setLanguage(uiState.streamData.subtitleLanguage)
                .setLabel(uiState.streamData.subtitleTitle)
                .setSelectionFlags(SELECTION_FLAG_DEFAULT)
                .build()
            mediaItem = mediaItem.buildUpon()
                .setSubtitleConfigurations(listOf(subtitleConfig))
                .build()
        }

        player.setMediaItem(mediaItem)
        player.prepare()
        if (args.resume && uiState.streamData.startedWatching) {
            player.seekTo(uiState.streamData.playedDuration)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.keepScreenOn = true
        subtitleView.let { sv ->
            val parent = view as? ViewGroup ?: return

            // Add to view hierarchy
            parent.addView(sv)
        }
        lifecycleScope.launch {
            PeriodicReminderUtility().getPeriodicReminder {
                if (player.isPlaying)
                    viewModel.updateLastPlayedPosition(player.currentPosition)
            }
        }
    }

    val playbackListener = object : Player.Listener {
        override fun onCues(cueGroup: CueGroup) {
            subtitleView.setCues(cueGroup.cues)
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
