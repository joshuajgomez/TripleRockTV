package com.joshgm3z.triplerocktv.compose.screens.player

import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.joshgm3z.triplerocktv.compose.R
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface

//import androidx.navigation.NavController
//import com.joshgm3z.netplayer.R
//import com.joshgm3z.netplayer.ui.util.DarkPreview
//import com.joshgm3z.netplayer.ui.util.DarkSurface
//import dagger.hilt.android.UnstableApi

private val sampleUrl =
    "https://rd2.seedr.cc/ff_get/3931638/5959460378/The.Drama.2026.1080p.WEBRip.AAC5.1.10bits.x265-Rapta.mkv"

val videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

@Composable
fun PlayerScreen2(
//    viewModel: PlaybackViewModel = hiltViewModel(),
//    trackViewModel: TrackSelectorViewModel,
//    navController: NavController
) {
    fun onBackPress() {
        /*navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("selectedStreamId", viewModel.streamId)*/
//        navController.popBackStack()
    }
    BackHandler {
        onBackPress()
    }

    /*val uiState by viewModel.playbackUiState.collectAsState()
    uiState?.let {
        val videoTitle = when (it.playbackItem) {
            is StreamData -> (it.playbackItem as StreamData).name
            is Episode -> (it.playbackItem as Episode).title
            else -> throw Exception("Unknown type of playbackItem")
        }
        trackViewModel.title = videoTitle
    }
    uiState?.videoUrl?.let { it ->*/
    PlaybackScreenContent(
        videoUrl = sampleUrl,
        resumePosition = null/*uiState?.resumePosition*/,
        updateLastPlayedPosition = {
//                viewModel.updateLastPlayedPosition(it)
        },
        onError = {
            /*navController.navigate(
                NavMainDestination.Error(
                    message = "Error playing video",
                    summary = it
                )
            )*/
        },
        onBackPress = {
            onBackPress()
        },
        onCaptionsClicked = {
            /*trackViewModel.loadTracksOfType(TrackType.Subtitle)
            navController.navigate(NavMainDestination.TrackSelector)*/
        },
//            subtitleTrackListener = trackViewModel.subtitleTrackListener,
//            trackToLoadFlow = trackViewModel.trackToLoad,
        updateSelectedSubtitle = { language, title, url ->
//                viewModel.updateSelectedSubtitle(language, title, url)
        }
    )
//    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlaybackScreenContent(
    videoUrl: String,
    resumePosition: Long? = null,
    updateLastPlayedPosition: (Long) -> Unit = {},
    onError: (String) -> Unit = {},
    onCaptionsClicked: () -> Unit = {},
    onBackPress: () -> Unit = {},
    updateSelectedSubtitle: (language: String, title: String, url: String?) -> Unit = { _, _, _ -> },
//    subtitleTrackListener: Player.Listener? = null,
//    trackToLoadFlow: StateFlow<LoadTrack?> = MutableStateFlow(null),
) {
    val context = LocalContext.current

    val renderersFactory = DefaultRenderersFactory(context).apply {
        // This allows the player to use software decoders if hardware fails
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context, renderersFactory).build().apply {

            val mediaItem = MediaItem.Builder()
                .setUri(videoUrl)
                .setMimeType(MimeTypes.VIDEO_MATROSKA) // Force MKV handling
                .build()

//            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            resumePosition?.let { seekTo(it) }
//            addListener(errorListener(onError = onError))
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
            val playerView = LayoutInflater.from(ctx)
                .inflate(R.layout.custom_player_view, null) as PlayerView
            playerView.apply {
                player = exoPlayer
                keepScreenOn = true
//                subtitleTrackListener?.let { player?.addListener(it) }
//                setShowSubtitleButton(true)
                findViewById<ImageButton>(R.id.custom_exo_subtitle)?.let {
                    it.setOnClickListener { onCaptionsClicked() }
                }
                /*findViewById<ImageButton>(R.id.iv_back_button)?.let {
                    it.setOnClickListener { onBackPress() }
                }*/
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )

    LaunchedEffect(Unit) {
        /*remindPeriodically {
            if (exoPlayer.isPlaying)
                updateLastPlayedPosition(exoPlayer.currentPosition)
        }*/
    }

    /*val trackToLoad by trackToLoadFlow.collectAsState()
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
    }*/
}

@DarkPreview
@Composable
private fun PreviewPlaybackScreenContent() {
    DarkSurface {
        PlaybackScreenContent("")
    }
}