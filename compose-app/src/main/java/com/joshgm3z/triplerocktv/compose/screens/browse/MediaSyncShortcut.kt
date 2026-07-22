package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.core.repository.StreamType

@Composable
fun MediaSyncShortcut(
    onSyncClick: () -> Unit = {},
    streamType: StreamType
) {
    val label = when (streamType) {
        StreamType.VideoOnDemand -> "movies"
        StreamType.LiveTV -> "live TV"
        StreamType.Series -> "series"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(70.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to 3RockTv",
            textAlign = TextAlign.Center,
            style = typography.titleLarge,
            color = colorScheme.onBackground
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = "Sync media with cloud to start watching $label",
            textAlign = TextAlign.Center,
            style = typography.bodyLarge,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.size(50.dp))
        PrimaryButton(
            onClick = onSyncClick,
            text = "Sync media",
            fillMaxWidth = false,
            imageVector = Icons.Default.CloudDownload
        )
    }
}