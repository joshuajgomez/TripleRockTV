package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.media3.common.C
import androidx.media3.common.text.CueGroup
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackUiState
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackType
import com.joshgm3z.triplerocktv.databinding.FragmentPlayerBinding
import com.joshgm3z.triplerocktv.util.setBackground
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.abs

const val FAST_FORWARD_DURATION_SHORT = 10000

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackFragment : Fragment() {

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

    private lateinit var binding: FragmentPlayerBinding

    private var viewVisibilityUpdateJob: Job? = null

    private lateinit var ccAction: PlaybackControlsRow.ClosedCaptioningAction

    private lateinit var audioAction: PlaybackControlsRow.ClosedCaptioningAction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.keepScreenOn = true
        initUi()
    }

    private fun initUi() {
        player.addListener(errorListener(this))
        player.addListener(playbackListener)
        player.addListener(trackViewModel.subtitleTrackListener)

        LeanbackPlayerAdapter(requireContext(), player, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
            transportControlGlue = createControlGlue(this)
        }

        val videoSupportFragment =
            childFragmentManager.findFragmentById(R.id.video_support_fragment) as? VideoSupportFragment

        transportControlGlue.host = VideoSupportFragmentGlueHost(videoSupportFragment)

        requireActivity().setBackground(null)

        viewModel.fetchStreamDetails(args.streamId, args.streamType, args.seriesId)
        lifecycleScope.launch {
            viewModel.playbackUiState.collectLatest {
                it?.let { playbackUiState ->
                    videoTitle = when (playbackUiState.playbackItem) {
                        is StreamData -> (playbackUiState.playbackItem as StreamData).name
                        is Episode -> (playbackUiState.playbackItem as Episode).title
                        else -> ""
                    }
                    val videoSubTitle = when (playbackUiState.playbackItem) {
                        is StreamData -> (playbackUiState.playbackItem as StreamData).movieMetadata?.genre
                        else -> ""
                    }
                    videoSubTitle?.let { subtitle ->
                        transportControlGlue.subtitle = subtitle
                    }
                    transportControlGlue.title = videoTitle
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
                val secondaryAdapter =
                    transportControlGlue.controlsRow?.secondaryActionsAdapter as? ArrayObjectAdapter
                secondaryAdapter?.clear()
                if (it.enableCaptionsButton) secondaryAdapter?.add(ccAction)
                if (it.enableAudioButton) secondaryAdapter?.add(audioAction)
            }
        }
        lifecycleScope.launch {
            PeriodicReminderUtility().getPeriodicReminder {
                if (player.isPlaying)
                    viewModel.updateLastPlayedPosition(player.currentPosition)
            }
        }
        subtitleView.let { sv ->
            val parent = view as? ViewGroup ?: return
            // Add to view hierarchy
            parent.addView(sv)
        }
    }

    private fun switchTrack(trackInfo: TrackInfo) {
        Logger.debug("trackInfo = [${trackInfo}]")
        val parametersBuilder = player.trackSelectionParameters.buildUpon()

        when (trackInfo.trackType) {
            TrackType.Subtitle -> {
                if (trackInfo.label == "Disabled") {
                    // Completely disable text tracks
                    parametersBuilder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                } else {
                    // Enable text tracks and prefer the selected language
                    parametersBuilder
                        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                        .setPreferredTextLanguage(trackInfo.language)
                }
            }

            TrackType.Audio -> {
                parametersBuilder.setPreferredAudioLanguage(trackInfo.language)
            }
        }

        player.trackSelectionParameters = parametersBuilder.build()
    }

    private fun loadSubtitle(subtitleData: SubtitleData) {
        Logger.debug("subtitleData = [${subtitleData}]")
        val currentMediaItem = player.currentMediaItem ?: return
        val currentPosition = player.currentPosition
        val playWhenReady = player.playWhenReady

        // 1. Create the subtitle configuration
        subtitleData.url ?: return
        val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(subtitleData.url!!.toUri())
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

    private fun Int.getDrawable() = ContextCompat.getDrawable(
        requireContext(), this
    )

    private fun createControlGlue(
        playerAdapter:
        LeanbackPlayerAdapter
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        ccAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
            icon = R.drawable.ic_subtitles.getDrawable()
        }
        audioAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
            icon = R.drawable.ic_voice.getDrawable()
        }
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireActivity(),
            playerAdapter
        ) {
            private val fastForwardAction = PlaybackControlsRow.FastForwardAction(context).apply {
                icon = R.drawable.skip_10s.getDrawable()
            }
            private val rewindAction = PlaybackControlsRow.RewindAction(context).apply {
                icon = R.drawable.rewind_10s.getDrawable()
            }

            override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
                adapter.add(rewindAction)
                super.onCreatePrimaryActions(adapter)
                adapter.add(fastForwardAction)
            }

            override fun onCreateSecondaryActions(adapter: ArrayObjectAdapter) {
                adapter.add(ccAction)
                adapter.add(audioAction)
            }

            override fun onActionClicked(action: Action) {
                when (action) {
                    ccAction -> {
                        videoTitle?.let { title ->
                            trackViewModel.loadTracksOfType(TrackType.Subtitle)
                            val action = PlaybackFragmentDirections.toTrackSelector()
                            action.title = title
                            findNavController().navigate(action)
                        }
                    }

                    audioAction -> {
                        trackViewModel.loadTracksOfType(TrackType.Audio)
                        val action = PlaybackFragmentDirections.toTrackSelector()
                        findNavController().navigate(action)
                    }

                    rewindAction -> skipBackward()

                    fastForwardAction -> skipForward()

                    else -> {
                        super.onActionClicked(action)
                    }
                }
            }
        }
    }

    private fun playVideo(uiState: PlaybackUiState) {
        Logger.info("uiState=[$uiState]")
        when (uiState.playbackItem) {
            is Episode -> playVideoFromSerisStream(
                uiState.playbackItem as Episode,
                uiState.videoUrl
            )

            is StreamData -> playVideoFromStreamData(
                uiState.playbackItem as StreamData,
                uiState.videoUrl
            )

            else -> throw Exception("Unknown playback item type: ${uiState.playbackItem::class.java}")
        }
    }

    private fun playVideoFromStreamData(
        streamData: StreamData,
        videoUrl: String
    ) {
        var mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()
        streamData.subtitleLanguage?.let {
            if (it.isEmpty()) return@let
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setPreferredTextLanguage(it)
                .build()
        }
        streamData.subtitleUrl?.let {
            if (it.isEmpty()) return@let
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(it.toUri())
                .setMimeType("application/x-subrip")
                .setLanguage(streamData.subtitleLanguage)
                .setLabel(streamData.subtitleTitle)
                .setSelectionFlags(SELECTION_FLAG_DEFAULT)
                .build()
            mediaItem = mediaItem.buildUpon()
                .setSubtitleConfigurations(listOf(subtitleConfig))
                .build()
        }

        player.setMediaItem(mediaItem)
        player.prepare()
        if (args.resume && streamData.startedWatching) {
            player.seekTo(streamData.playedDuration)
        }
    }

    private fun playVideoFromSerisStream(
        episode: Episode,
        videoUrl: String
    ) {
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        if (args.resume && episode.startedWatching) {
            player.seekTo(episode.playedDuration)
        }
    }


    val playbackListener = object : Player.Listener {
        override fun onCues(cueGroup: CueGroup) {
            subtitleView.setCues(cueGroup.cues)
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                val skippedDuration = newPosition.positionMs - oldPosition.positionMs
                Logger.debug("skippedDuration = [$skippedDuration]")
                val durationSec = abs(skippedDuration) / 1000
                // round off to nearest 10
                val roundedSec = ((durationSec + 5) / 10) * 10
                when {
                    roundedSec !in 10..80 -> {}
                    skippedDuration > 0 -> binding.tvSkipForward.setVisibleForDuration("+ ${roundedSec}s")
                    else -> binding.tvSkipBack.setVisibleForDuration("- ${roundedSec}s")
                }
            }
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        }
    }

    private fun skipBackward() {
        val newPos = (player.currentPosition - FAST_FORWARD_DURATION_SHORT).coerceAtLeast(0)
        player.seekTo(newPos)
    }

    private fun skipForward() {
        val newPos = (player.currentPosition + FAST_FORWARD_DURATION_SHORT).coerceAtLeast(0)
        player.seekTo(newPos)
    }

    private fun View.setVisibleForDuration(
        text: String,
        duration: Long = 800L
    ) {
        val view = this as TextView
        view.setVisible(true)
        view.text = text
        viewVisibilityUpdateJob?.cancel()
        viewVisibilityUpdateJob = lifecycleScope.launch {
            delay(duration)
            view.setVisible(false)
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseLogger.logScreenView(ScreenName.Player)
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
