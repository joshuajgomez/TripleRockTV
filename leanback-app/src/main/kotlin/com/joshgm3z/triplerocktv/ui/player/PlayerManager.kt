package com.joshgm3z.triplerocktv.ui.player

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.media3.common.C.SELECTION_FLAG_DEFAULT
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.NavDirections
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreLogger
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.errorListener
import com.joshgm3z.triplerocktv.core.util.loadSubtitle
import com.joshgm3z.triplerocktv.core.util.remindPeriodically
import com.joshgm3z.triplerocktv.core.util.switchTrack
import com.joshgm3z.triplerocktv.core.viewmodel.LoadTrack
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackUiState
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackType
import com.joshgm3z.triplerocktv.util.setVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs
import kotlin.ranges.contains

private const val FAST_FORWARD_DURATION_SHORT = 10000

@OptIn(UnstableApi::class)
class PlayerManager
@Inject
constructor(
    private val scope: CoroutineScope,
) {

    private lateinit var player: ExoPlayer

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>

    private lateinit var ccAction: PlaybackControlsRow.ClosedCaptioningAction

    private lateinit var audioAction: PlaybackControlsRow.ClosedCaptioningAction

    private lateinit var subtitleView: SubtitleView

    private var videoTitle: String? = null

    private var viewVisibilityUpdateJob: Job? = null

    private var playbackUiState: PlaybackUiState? = null

    private lateinit var playbackViewModel: PlaybackViewModel

    private lateinit var trackSelectorViewModel: TrackSelectorViewModel

    @Inject
    lateinit var firestoreLogger: FirestoreLogger

    fun init(
        context: Context,
        view: View,
        playbackViewModel: PlaybackViewModel,
        trackSelectorViewModel: TrackSelectorViewModel,
        videoSupportFragment: VideoSupportFragment,
        navigate: (NavDirections) -> Unit,
        tvSkipForward: TextView,
        tvSkipBack: TextView,
    ) {
        subtitleView = SubtitleView(context).apply {
            setUserDefaultStyle()
            setUserDefaultTextSize()
        }
        player = ExoPlayer.Builder(context).build()
        this.playbackViewModel = playbackViewModel
        this.trackSelectorViewModel = trackSelectorViewModel
        player.addListener(
            errorListener(onError = {
                firestoreLogger.log(mapOf("playback_error" to it))
                navigate(PlaybackFragmentDirections.toError(it))
            })
        )
        player.addListener(playbackListener(tvSkipForward, tvSkipBack))
        player.addListener(trackSelectorViewModel.subtitleTrackListener)

        LeanbackPlayerAdapter(context, player, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
            transportControlGlue = createControlGlue(
                context,
                this,
                trackSelectorViewModel,
                navigate
            )
        }

        transportControlGlue.host = VideoSupportFragmentGlueHost(videoSupportFragment)

        scope.launch {
            trackSelectorViewModel.trackToLoad.collectLatest { it ->
                Logger.debug("subtitleTrackToLoad $it")
                it?.let {
                    when (it) {
                        is LoadTrack.OnlineSubtitle -> with(it.subtitleData) {
                            player.loadSubtitle(this)
                            playbackViewModel.updateSelectedSubtitle(language ?: "", title, url)
                        }

                        is LoadTrack.OfflineTrack -> with(it.trackInfo) {
                            player.switchTrack(this)
                            if (trackType == TrackType.Subtitle) playbackViewModel.updateSelectedSubtitle(
                                language ?: "",
                                label ?: "",
                                null
                            )
                        }
                    }
                }
            }
        }
        scope.launch {
            trackSelectorViewModel.trackButtonState.collectLatest {
                Logger.debug("trackButtonState $it")
                val secondaryAdapter =
                    transportControlGlue.controlsRow?.secondaryActionsAdapter as? ArrayObjectAdapter
                secondaryAdapter?.clear()
                withContext(Dispatchers.Main) {
                    if (it.enableCaptionsButton) secondaryAdapter?.add(ccAction)
                    if (it.enableAudioButton) secondaryAdapter?.add(audioAction)
                }
            }
        }
        scope.launch {
            remindPeriodically {
                scope.launch(Dispatchers.Main) {
                    if (player.isPlaying)
                        playbackViewModel.updateLastPlayedPosition(player.currentPosition)
                }
            }
        }
        subtitleView.let { sv ->
            val parent = view as? ViewGroup ?: return
            // Add to view hierarchy
            parent.addView(sv)
        }
    }

    fun playVideo(resume: Boolean) {
        Logger.info("resume=[$resume]")
        scope.launch {
            playbackViewModel.playbackUiState.collectLatest {
                it?.let { playbackUiState ->
                    videoTitle = when (playbackUiState.playbackItem) {
                        is StreamData -> (playbackUiState.playbackItem as StreamData).name
                        is Episode -> (playbackUiState.playbackItem as Episode).title
                        else -> ""
                    }
                    trackSelectorViewModel.title = videoTitle
                    val videoSubTitle = when (playbackUiState.playbackItem) {
                        is StreamData -> (playbackUiState.playbackItem as StreamData).movieMetadata?.genre
                        else -> ""
                    }
                    videoSubTitle?.let { subtitle ->
                        transportControlGlue.subtitle = subtitle
                    }
                    transportControlGlue.title = videoTitle
                    transportControlGlue.isSeekEnabled = true
                    withContext(Dispatchers.Main) {
                        transportControlGlue.playWhenPrepared()
                    }
                    when (playbackUiState.playbackItem) {
                        is Episode -> withContext(Dispatchers.Main) {
                            playVideoFromSerisStream(
                                resume,
                                playbackUiState.playbackItem as Episode,
                                playbackUiState.videoUrl
                            )
                        }

                        is StreamData -> withContext(Dispatchers.Main) {
                            playVideoFromStreamData(
                                resume,
                                playbackUiState.playbackItem as StreamData,
                                playbackUiState.videoUrl
                            )
                        }

                        else -> throw Exception("Unknown playback item type: ${playbackUiState.playbackItem::class.java}")
                    }
                }
            }
        }
    }

    private fun Int.getDrawable(context: Context) = ContextCompat.getDrawable(
        context, this
    )

    private fun createControlGlue(
        context: Context,
        playerAdapter: LeanbackPlayerAdapter,
        trackViewModel: TrackSelectorViewModel,
        navigate: (NavDirections) -> Unit,
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        ccAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
            icon = R.drawable.ic_cc.getDrawable(context)
        }
        audioAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
            icon = R.drawable.ic_audio.getDrawable(context)
        }
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            context,
            playerAdapter
        ) {
            private val fastForwardAction =
                PlaybackControlsRow.FastForwardAction(context).apply {
                    icon = R.drawable.forward_10s.getDrawable(context)
                }
            private val rewindAction = PlaybackControlsRow.RewindAction(context).apply {
                icon = R.drawable.replay_10s.getDrawable(context)
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
                            val action = PlaybackFragmentDirections.toTrackSelector(title)
                            navigate(action)
                        }
                    }

                    audioAction -> {
                        trackViewModel.loadTracksOfType(TrackType.Audio)
                        val action = PlaybackFragmentDirections.toTrackSelector("")
                        navigate(action)
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

    private fun playVideoFromStreamData(
        resume: Boolean,
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
                .setId("online")
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
        if (resume && streamData.startedWatching) {
            streamData.recentlyPlayed?.playedDuration?.let {
                player.seekTo(it)
            }
        }
    }

    private fun playVideoFromSerisStream(
        resume: Boolean,
        episode: Episode,
        videoUrl: String
    ) {
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        if (resume && episode.startedWatching) {
            episode.recentlyPlayed?.playedDuration?.let {
                player.seekTo(it)
            }
        }
    }


    private fun playbackListener(
        tvSkipForward: TextView,
        tvSkipBack: TextView,
    ) = object : Player.Listener {
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
                    skippedDuration > 0 -> tvSkipForward.setVisibleForDuration("+ ${roundedSec}s")
                    else -> tvSkipBack.setVisibleForDuration("- ${roundedSec}s")
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
        viewVisibilityUpdateJob = scope.launch {
            delay(duration)
            view.setVisible(false)
        }
    }

    fun onPause() {
        playbackViewModel.updateLastPlayedPosition(player.currentPosition)
        transportControlGlue.pause()
    }

    fun onDestroy() {
        player.release()
    }
}