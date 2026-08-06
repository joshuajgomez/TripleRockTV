package com.joshgm3z.triplerocktv.compose.screens.player.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.CloseButton
import com.joshgm3z.triplerocktv.compose.screens.common.DarkLandscapePreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.SecondaryButton
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.viewmodel.ListState
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorUiState
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel

@Composable
fun TrackSelectorDialog(
    viewModel: TrackSelectorViewModel,
    goBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState == null) goBack()
    else Dialog(
        onDismissRequest = goBack,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        TrackSelectorDialogContent(
            uiState = uiState!!,
            onBackPress = goBack,
            onDownloadSubtitleClicked = { viewModel.onDownloadedSubtitleClick(it) },
            onFindMoreClicked = { viewModel.onFindMoreClicked() },
            onTrackClicked = { viewModel.onTrackClicked(it) },
            onLanguageClick = { viewModel.onLanguageClick(it) }
        )
    }
}

@Composable
fun TrackSelectorDialogContent(
    uiState: TrackSelectorUiState,
    onDownloadSubtitleClicked: (SubtitleData) -> Unit = {},
    onFindMoreClicked: () -> Unit = {},
    onTrackClicked: (TrackInfo) -> Unit = {},
    onBackPress: () -> Unit = {},
    onLanguageClick: (String?) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .width(600.dp)
            .height(300.dp)
            .background(color = colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 15.dp, horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopRow(
                title = when (uiState.listState) {
                    is ListState.SubtitleTracks -> "Subtitle tracks"
                    is ListState.OnlineSubtitleTracks -> "Results from OpenSubtitles.com"
                    is ListState.AudioTracks -> "Audio tracks"
                    else -> "Unknown"
                },
                onBackPress = onBackPress,
                onFindMoreClicked = onFindMoreClicked,
                showFindMoreButton = uiState.listState is ListState.SubtitleTracks
            )
            Spacer(Modifier.size(10.dp))
            when (uiState.listState) {
                is ListState.OnlineSubtitleTracks -> SubtitleDownloaderContent(
                    listState = uiState.listState as ListState.OnlineSubtitleTracks,
                    onClick = onDownloadSubtitleClicked,
                    onLanguageClick = onLanguageClick
                )

                is ListState.SubtitleTracks -> SubtitleTracks(
                    listState = uiState.listState as ListState.SubtitleTracks,
                    onClick = onTrackClicked,
                )

                is ListState.AudioTracks -> AudioTracks(
                    listState = uiState.listState as ListState.AudioTracks,
                    onClick = onTrackClicked,
                )

                else -> {}
            }
        }
        if (uiState.isLoading) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
            ) {}
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TopRow(
    title: String,
    onBackPress: () -> Unit,
    onFindMoreClicked: () -> Unit,
    showFindMoreButton: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton { onBackPress() }
        Spacer(Modifier.size(15.dp))
        Text(
            text = title,
            style = typography.titleMedium,
            color = textColor(),
            textAlign = TextAlign.Center,
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (showFindMoreButton) SecondaryButton(
            text = "OpenSubtitles.com",
            fillMaxWidth = false,
            imageVector = Icons.Default.Search
        ) {
            onFindMoreClicked()
        }
    }
}

@DarkLandscapePreview
@Composable
fun PreviewSubtitleDownloaderDialog() {
    DarkSurface {
        TrackSelectorDialogContent(
            uiState = TrackSelectorUiState(
                isLoading = false,
                listState = ListState.OnlineSubtitleTracks(
                    listOf(
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                    )
                ),
            )
        )
    }
}
