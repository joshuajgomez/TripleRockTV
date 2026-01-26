package com.joshgm3z.triplerocktv.ui.loading

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.login.FakeLoginViewModel
import com.joshgm3z.triplerocktv.ui.login.ILoginViewModel
import com.joshgm3z.triplerocktv.ui.login.LoginViewModel
import com.joshgm3z.triplerocktv.ui.player.FakeMediaInfoViewModel
import com.joshgm3z.triplerocktv.ui.theme.Gray800
import com.joshgm3z.triplerocktv.ui.theme.Green10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import kotlinx.coroutines.delay

@Composable
fun getMediaLoadingViewModel(): IMediaLoadingViewModel = when {
    LocalInspectionMode.current -> FakeMediaLoadingViewModel()
    else -> hiltViewModel<MediaLoadingViewModel>()
}

@Composable
fun MediaLoadingScreen(
    viewModel: IMediaLoadingViewModel = getMediaLoadingViewModel(),
    onMediaLoaded: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                when (val uiState = viewModel.uiState.collectAsState().value) {
                    is MediaLoadingUiState.Update -> {
                        ShowLoadingUpdates(uiState) {}
                    }

                    is MediaLoadingUiState.Error -> {
                        ShowError(uiState.message, uiState.summary) {
                            viewModel.fetchContent()
                        }
                    }

                    else -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Please wait")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ShowError(
    message: String,
    summary: String,
    onClickRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Warning, contentDescription = null,
                tint = colorScheme.errorContainer
            )
            Spacer(Modifier.size(10.dp))
            Text(
                message,
                fontSize = 20.sp
            )
        }
        Spacer(Modifier.size(10.dp))
        Text(
            summary,
            color = colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 15.sp
        )
        Spacer(Modifier.size(20.dp))
        Button(onClick = { onClickRetry() }) {
            Text("Try again")
        }
    }
}


@Composable
private fun ShowLoadingUpdates(
    uiState: MediaLoadingUiState.Update,
    onMediaLoaded: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Fetching content", fontSize = 30.sp)
        Spacer(Modifier.size(70.dp))
        val stateMap = uiState.map
        if (stateMap.isNotEmpty() && stateMap.all { it.value.status == LoadingStatus.Complete }) {
            LaunchedEffect(Unit) {
                delay(500)
                onMediaLoaded()
            }
        }
        Column {
            stateMap.forEach { (type, state) ->
                AnimatedVisibility(state.status != LoadingStatus.Initial) {
                    LoadingBar(
                        type.label,
                        state
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingBar(
    title: String,
    loadingState: LoadingState,
) {
    val bgColor = when (loadingState.status) {
        LoadingStatus.Ongoing -> colorScheme.primaryContainer.copy(alpha = 0.2f)
        else -> Gray800
    }
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .width(400.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(if (loadingState.status == LoadingStatus.Ongoing) loadingState.percent / 100f else 0f)
                .fillMaxHeight()
                .background(color = colorScheme.primaryContainer)
        ) { }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(loadingState.status != LoadingStatus.Initial) {
                StatusIcon(loadingState.status)
            }
            Spacer(Modifier.size(10.dp))
            Text(title, fontSize = 20.sp)
            if (loadingState.status == LoadingStatus.Ongoing)
                Text(" ${loadingState.percent}%", fontSize = 20.sp)
        }
    }
}

@Composable
fun StatusIcon(status: LoadingStatus) {
    Icon(
        imageVector = when (status) {
            LoadingStatus.Complete -> Icons.Default.CheckCircle
            LoadingStatus.Error -> Icons.Default.Warning
            else -> Icons.AutoMirrored.Default.KeyboardArrowRight
        },
        contentDescription = null,
        tint = when (status) {
            LoadingStatus.Complete -> Green10
            LoadingStatus.Error -> colorScheme.errorContainer
            else -> colorScheme.onBackground
        },
    )
}

@Composable
@TvPreview
private fun PreviewMediaLoadingScreen() {
    TripleRockTVTheme {
        MediaLoadingScreen()
    }
}