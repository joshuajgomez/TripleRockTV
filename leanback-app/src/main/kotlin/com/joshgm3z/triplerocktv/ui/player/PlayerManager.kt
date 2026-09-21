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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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

private const val FAST_FORWARD_DURATION_SHORT = 10000

@OptIn(UnstableApi::class)
class PlayerManager @Inject constructor(
    private val scope: CoroutineScope,
) : DefaultLifecycleObserver {

    private var player: ExoPlayer? = null
    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var ccAction: PlaybackControlsRow.ClosedCaptioningAction
    private lateinit var audioAction: PlaybackControlsRow.ClosedCaptioningAction
    private lateinit var subtitleView: SubtitleView

    private var videoTitle: String? = null
    private lateinit var playbackViewModel: PlaybackViewModel
    private lateinit var trackSelectorViewModel: TrackSelectorViewModel

    private var viewVisibilityUpdateJob: Job? = null

    @Inject
    lateinit var firestoreLogger: FirestoreLogger

    fun init(
        context: Context,
        view: View,
        lifecycleOwner: LifecycleOwner,
        playbackViewModel: PlaybackViewModel,
        trackSelectorViewModel: TrackSelectorViewModel,
        videoSupportFragment: VideoSupportFragment,
        navigate: (NavDirections) -> Unit,
        tvSkipForward: TextView,
        tvSkipBack: TextView,
    ) {
        // Register observer to handle release() automatically
        lifecycleOwner.lifecycle.addObserver(this)

        this.playbackViewModel = playbackViewModel
        this.trackSelectorViewModel = trackSelectorViewModel

        player = ExoPlayer.Builder(context).build().apply {
            addListener(errorListener {
                firestoreLogger.log(mapOf("playback_error" to it))
                navigate(PlaybackFragmentDirections.toError(it))
            })
            addListener(playbackListener(tvSkipForward, tvSkipBack))
            addListener(trackSelectorViewModel.subtitleTrackListener)
        }

        subtitleView = SubtitleView(context).apply {
            setUserDefaultStyle()
            setUserDefaultTextSize()
            (view as? ViewGroup)?.addView(this)
        }

        val playerAdapter = LeanbackPlayerAdapter(context, player!!, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
        }

        transportControlGlue =
            createControlGlue(context, playerAdapter, trackSelectorViewModel, navigate)
        transportControlGlue.host = VideoSupportFragmentGlueHost(videoSupportFragment)

        observeDataStreams()
    }

    private fun observeDataStreams() {
        scope.launch {
            trackSelectorViewModel.trackToLoad.collectLatest { track ->
                track?.let {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is LoadTrack.OnlineSubtitle -> {
                                player?.loadSubtitle(it.subtitleData)
                                playbackViewModel.updateSelectedSubtitle(
                                    it.subtitleData.language ?: "",
                                    it.subtitleData.title,
                                    it.subtitleData.url
                                )
                            }

                            is LoadTrack.OfflineTrack -> {
                                player?.switchTrack(it.trackInfo)
                                if (it.trackInfo.trackType == TrackType.Subtitle) {
                                    playbackViewModel.updateSelectedSubtitle(
                                        it.trackInfo.language ?: "",
                                        it.trackInfo.label ?: "",
                                        null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        scope.launch {
            trackSelectorViewModel.trackButtonState.collectLatest { state ->
                val adapter =
                    transportControlGlue.controlsRow?.secondaryActionsAdapter as? ArrayObjectAdapter
                adapter?.apply {
                    clear()
                    withContext(Dispatchers.Main) {
                        if (state.enableCaptionsButton) add(ccAction)
                        if (state.enableAudioButton) add(audioAction)
                    }
                }
            }
        }

        scope.launch {
            remindPeriodically {
                scope.launch(Dispatchers.Main) {
                    if (player?.isPlaying == true) {
                        playbackViewModel.updateLastPlayedPosition(player?.currentPosition ?: 0L)
                    }
                }
            }
        }
    }

    fun playVideo(resume: Boolean) {
        scope.launch {
            playbackViewModel.playbackUiState.collectLatest { state ->
                state?.let { uiState ->
                    setupMetadata(uiState)
                    prepareMedia(resume, uiState)
                }
            }
        }
    }

    private fun setupMetadata(state: PlaybackUiState) {
        videoTitle = when (val item = state.playbackItem) {
            is StreamData -> item.name
            is Episode -> item.title
            else -> ""
        }
        trackSelectorViewModel.title = videoTitle
        transportControlGlue.title = videoTitle
        transportControlGlue.subtitle =
            (state.playbackItem as? StreamData)?.movieMetadata?.genre ?: ""
        transportControlGlue.isSeekEnabled = true
        scope.launch(Dispatchers.Main) {
            transportControlGlue.playWhenPrepared()
        }
    }

    private fun prepareMedia(resume: Boolean, state: PlaybackUiState) {
        val mediaItemBuilder = MediaItem.Builder().setUri(state.videoUrl)

        if (state.playbackItem is StreamData) {
            val stream = state.playbackItem as StreamData
            stream.subtitleLanguage?.takeIf { it.isNotEmpty() }?.let { lang ->
                player?.trackSelectionParameters = player?.trackSelectionParameters
                    ?.buildUpon()?.setPreferredTextLanguage(lang)?.build()!!
            }
            stream.subtitleUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                val subConfig = MediaItem.SubtitleConfiguration.Builder(url.toUri())
                    .setMimeType("application/x-subrip")
                    .setLanguage(stream.subtitleLanguage)
                    .setSelectionFlags(SELECTION_FLAG_DEFAULT)
                    .build()
                mediaItemBuilder.setSubtitleConfigurations(listOf(subConfig))
            }
        }

        scope.launch(Dispatchers.Main) {
            player?.apply {
                setMediaItem(mediaItemBuilder.build())
                prepare()

                val lastPos = when (val item = state.playbackItem) {
                    is Episode -> if (resume && item.startedWatching) item.recentlyPlayed?.playedDuration else null
                    is StreamData -> if (resume && item.startedWatching) item.recentlyPlayed?.playedDuration else null
                    else -> null
                }
                lastPos?.let { seekTo(it) }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        player?.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        player?.release()
        player = null
    }

    private fun playbackListener(tvForward: TextView, tvBack: TextView) = object : Player.Listener {
        override fun onCues(cueGroup: CueGroup) {
            subtitleView.setCues(cueGroup.cues)
        }

        override fun onPositionDiscontinuity(
            oldPos: Player.PositionInfo,
            newPos: Player.PositionInfo,
            reason: Int
        ) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                val diff = newPos.positionMs - oldPos.positionMs
                val roundedSec = ((abs(diff) / 1000 + 5) / 10) * 10
                if (roundedSec in 10..80) {
                    if (diff > 0) tvForward.setVisibleForDuration("+${roundedSec}s")
                    else tvBack.setVisibleForDuration("-${roundedSec}s")
                }
            }
        }
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

    private fun createControlGlue(
        context: Context,
        adapter: LeanbackPlayerAdapter,
        trackVM: TrackSelectorViewModel,
        navigate: (NavDirections) -> Unit
    ) = object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

        private val fastForwardAction = PlaybackControlsRow.FastForwardAction(context).apply {
            icon = ContextCompat.getDrawable(context, R.drawable.forward_10s)
        }
        private val rewindAction = PlaybackControlsRow.RewindAction(context).apply {
            icon = ContextCompat.getDrawable(context, R.drawable.replay_10s)
        }

        init {
            ccAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_cc)
            }
            audioAction = PlaybackControlsRow.ClosedCaptioningAction(context).apply {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_audio)
            }
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
                    videoTitle?.let {
                        trackVM.loadTracksOfType(TrackType.Subtitle)
                        navigate(PlaybackFragmentDirections.toTrackSelector(it))
                    }
                }

                audioAction -> {
                    trackVM.loadTracksOfType(TrackType.Audio)
                    navigate(PlaybackFragmentDirections.toTrackSelector(""))
                }

                rewindAction -> skipBackward()
                fastForwardAction -> skipForward()
                else -> super.onActionClicked(action)
            }
        }
    }

    private fun skipBackward() {
        player?.let {
            val newPos = (it.currentPosition - FAST_FORWARD_DURATION_SHORT).coerceAtLeast(0)
            it.seekTo(newPos)
        }
    }

    private fun skipForward() {
        player?.let {
            val newPos = (it.currentPosition + FAST_FORWARD_DURATION_SHORT).coerceAtLeast(0)
            it.seekTo(newPos)
        }
    }
}
