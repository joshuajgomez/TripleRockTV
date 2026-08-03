package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSyncExitDialog(
    onExitScreenClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    AlertDialog(
        title = {
            Text(text = "Stop sync and exit?", style = typography.titleLarge)
        },
        text = {
            Text(text = "Sync is still ongoing. If you exit the screen, the ongoing sync will be stopped.")
        },
        confirmButton = {
            PrimaryButton(
                text = "Exit screen",
                fillMaxWidth = false,
                modifier = Modifier.width(130.dp)
            ) { onExitScreenClick() }
        },
        dismissButton = {
            SecondaryButton(
                text = "Back",
                fillMaxWidth = false,
                modifier = Modifier.width(130.dp)
            ) { onBackClick() }
        },
        onDismissRequest = onBackClick,
    )
}

@DarkPreview
@Composable
private fun PreviewConfirmationDialog() {
    DarkSurface {
        MediaSyncExitDialog()
    }
}