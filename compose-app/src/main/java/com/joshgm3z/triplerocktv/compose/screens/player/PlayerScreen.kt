package com.joshgm3z.triplerocktv.compose.screens.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.errorListener
import com.joshgm3z.triplerocktv.core.util.loadSubtitle
import com.joshgm3z.triplerocktv.core.util.remindPeriodically
import com.joshgm3z.triplerocktv.core.util.switchTrack
import com.joshgm3z.triplerocktv.core.viewmodel.LoadTrack
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlayerScreen(
    viewModel: PlaybackViewModel = hiltViewModel(),
    trackViewModel: TrackSelectorViewModel,
    navController: NavController
) {
    RotateToLandscape()
    BackHandler {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("selectedStreamId", viewModel.streamId)
        navController.popBackStack()
    }

    val uiState by viewModel.playbackUiState.collectAsState()
    uiState?.let {
        val videoTitle = when (it.playbackItem) {
            is StreamData -> (it.playbackItem as StreamData).name
            is Episode -> (it.playbackItem as Episode).title
            else -> throw Exception("Unknown type of playbackItem")
        }
        trackViewModel.title = videoTitle
    }
    uiState?.videoUrl?.let { it ->
        PlaybackScreenContent(
            videoUrl = it,
            resumePosition = uiState?.resumePosition,
            updateLastPlayedPosition = {
                viewModel.updateLastPlayedPosition(it)
            },
            onError = {
                navController.navigate(
                    NavMainDestination.Error(
                        message = "Error playing video",
                        summary = it
                    )
                )
            },
            onCaptionsClicked = {
                trackViewModel.loadTracksOfType(TrackType.Subtitle)
                navController.navigate(NavMainDestination.TrackSelector)
            },
            subtitleTrackListener = trackViewModel.subtitleTrackListener,
            trackToLoadFlow = trackViewModel.trackToLoad,
            updateSelectedSubtitle = { language, title, url ->
                viewModel.updateSelectedSubtitle(language, title, url)
            }
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlaybackScreenContent(
    videoUrl: String,
    resumePosition: Long? = null,
    updateLastPlayedPosition: (Long) -> Unit = {},
    onError: (String) -> Unit = {},
    onCaptionsClicked: () -> Unit = {},
    updateSelectedSubtitle: (language: String, title: String, url: String?) -> Unit = { _, _, _ -> },
    subtitleTrackListener: Player.Listener? = null,
    trackToLoadFlow: StateFlow<LoadTrack?> = MutableStateFlow(null),
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            resumePosition?.let { seekTo(it) }
            playWhenReady = true
            addListener(errorListener(onError = onError))
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        updateLastPlayedPosition(exoPlayer.currentPosition)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                keepScreenOn = true
                subtitleTrackListener?.let { player?.addListener(it) }
                setShowSubtitleButton(true)
                findViewById<View>(androidx.media3.ui.R.id.exo_subtitle)?.let {
                    it.isEnabled = true
                    it.setOnClickListener { onCaptionsClicked() }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )

    LaunchedEffect(Unit) {
        remindPeriodically {
            if (exoPlayer.isPlaying)
                updateLastPlayedPosition(exoPlayer.currentPosition)
        }
    }

    val trackToLoad by trackToLoadFlow.collectAsState()
    trackToLoad?.let {
        when (it) {
            is LoadTrack.OnlineSubtitle -> with(it.subtitleData) {
                exoPlayer.loadSubtitle(this)
                updateSelectedSubtitle(language ?: "", title, url)
            }

            is LoadTrack.OfflineTrack -> with(it.trackInfo) {
                exoPlayer.switchTrack(this)
                if (trackType == TrackType.Subtitle) updateSelectedSubtitle(
                    language ?: "",
                    label ?: "",
                    null
                )
            }
        }
    }
}


@Composable
fun RotateToLandscape() {
    if (isDevBuild) return
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity?.requestedOrientation
            ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@DarkPreview
@Composable
private fun PreviewPlaybackScreenContent() {
    DarkSurface {
        PlaybackScreenContent("")
    }
}