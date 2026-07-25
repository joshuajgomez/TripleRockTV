package com.joshgm3z.triplerocktv.compose.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import com.joshgm3z.triplerocktv.core.util.remindPeriodically
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel

@Composable
fun PlaybackScreen(
    viewModel: PlaybackViewModel = hiltViewModel(),
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
    uiState?.videoUrl?.let { it ->
        PlaybackScreenContent(it) {
            viewModel.updateLastPlayedPosition(it)
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

@Composable
private fun PlaybackScreenContent(
    videoUrl: String,
    updateLastPlayedPosition: (Long) -> Unit = {}
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
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
}

@DarkPreview
@Composable
private fun PreviewPlaybackScreenContent() {
    TripleRockTvTheme {
        PlaybackScreenContent("")
    }
}