package com.joshgm3z.triplerocktv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray800
import com.joshgm3z.triplerocktv.ui.theme.Green10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.LoadingState
import com.joshgm3z.triplerocktv.viewmodel.LoadingStatus
import com.joshgm3z.triplerocktv.viewmodel.MediaLoadingViewModel

@Composable
fun MediaLoadingScreen(
    viewModel: MediaLoadingViewModel = hiltViewModel(),
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Please wait", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Fetching content", color = colorScheme.onBackground.copy(alpha = 0.5f))
            }
            Spacer(Modifier.size(50.dp))
            Column {
                val uiState = viewModel.uiState.collectAsState().value
                AnimatedVisibility(uiState.videoLoadingState.status != LoadingStatus.Initial) {
                    LoadingBar(
                        "Video on demand",
                        uiState.videoLoadingState
                    )
                }
                AnimatedVisibility(uiState.seriesLoadingState.status != LoadingStatus.Initial) {
                    LoadingBar(
                        "Series",
                        uiState.seriesLoadingState
                    )
                }
                AnimatedVisibility(uiState.liveTvLoadingState.status != LoadingStatus.Initial) {
                    LoadingBar(
                        "Live TV",
                        uiState.liveTvLoadingState
                    )
                }
                AnimatedVisibility(uiState.parsingState.status != LoadingStatus.Initial) {
                    LoadingBar(
                        "Parsing playlist",
                        uiState.parsingState
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