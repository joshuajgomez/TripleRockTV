package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.viewmodel.DownloadedItemState
import com.joshgm3z.triplerocktv.core.viewmodel.DownloaderUiState
import com.joshgm3z.triplerocktv.core.viewmodel.UpdaterViewModel

@Composable
fun MediaSyncScreen(
    viewModel: UpdaterViewModel = hiltViewModel(),
    onSyncComplete: () -> Unit = {},
    onBackPress: () -> Unit = {},
    showExitDialog: () -> Unit = {},
) {
    viewModel.uiState.collectAsState().value.let { uiState ->
        fun onBackPressWrapper() {
            if (!uiState.enableButtons) showExitDialog()
            else onBackPress()
        }
        BackHandler(enabled = !uiState.enableButtons) {
            showExitDialog()
        }
        MediaSyncScreenContent(
            uiState = uiState,
            onDownloadClick = {
                viewModel.startUpdate(it)
            },
            onBackPress = { onBackPressWrapper() }
        )
    }
}

@Composable
private fun MediaSyncScreenContent(
    uiState: DownloaderUiState = DownloaderUiState(),
    onDownloadClick: (List<StreamType>) -> Unit = {},
    onBackPress: () -> Unit = {},
) {
    SettingScaffold(
        title = "Media Sync",
        onBackClick = onBackPress
    ) {
        LazyColumn {
            items(uiState.stateMap.keys.toList()) { type ->
                SyncItem(
                    type,
                    uiState.stateMap[type] ?: return@items,
                    enabled = uiState.enableButtons,
                    onDownloadClick = { onDownloadClick(listOf(type)) }
                )
            }
        }
        Spacer(
            Modifier
                .fillMaxSize()
                .weight(1f)
        )
        PrimaryButton(
            enabled = uiState.enableButtons,
            onClick = { onDownloadClick(uiState.stateMap.keys.toList()) },
            modifier = Modifier.fillMaxWidth(),
            text = "Update all"
        )
    }
}

val cardCornerRadius = 5.dp

@Composable
fun SyncItem(
    streamType: StreamType,
    state: DownloadedItemState,
    enabled: Boolean,
    selected: Boolean = state.loadingStatus == LoadingStatus.Ongoing,
    onDownloadClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .background(
                color = if (selected) colorScheme.onPrimary
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = colorScheme.onBackground.copy(alpha = 0.4f),
                shape = RoundedCornerShape(cardCornerRadius)
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = streamType.name,
                color = colorScheme.onBackground,
                style = typography.titleMedium
            )
            val status = buildAnnotatedString {
                append(state.filesCount ?: "")
                if (state.filesCount != null)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("  •  ")
                    }
                append(state.status)
            }
            Text(
                text = status,
                color = colorScheme.onBackground.copy(alpha = 0.5f),
                style = typography.bodySmall
            )
        }
        AnimatedVisibility(visible = enabled) {
            IconButton(
                onClick = { onDownloadClick() },
                modifier = Modifier
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .size(40.dp),
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewMediaSyncScreen() {
    DarkSurface {
        MediaSyncScreenContent(
            DownloaderUiState(
                enableButtons = true,
                stateMap = mapOf(
                    StreamType.VideoOnDemand to DownloadedItemState(
                        status = "Updating",
                        filesCount = "100 videos",
                        loadingStatus = LoadingStatus.Complete
                    ),
                    StreamType.Series to DownloadedItemState(
                        status = "Couldn't update",
                        filesCount = "100 videos",
                        loadingStatus = LoadingStatus.Error
                    ),
                    StreamType.LiveTV to DownloadedItemState(
                        status = "Tap to fetch videos",
                        filesCount = null,
                        loadingStatus = LoadingStatus.Complete
                    ),
                ),
            )
        )
    }
}