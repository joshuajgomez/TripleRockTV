package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor

@Composable
fun ErrorDialog(
    message: String,
    summary: String?,
    onDismissClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .background(color = cardColor())
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null
            )
            Spacer(Modifier.size(15.dp))
            Text(
                text = message,
                style = typography.titleMedium,
                color = textColor()
            )
            summary?.let {
                Text(
                    text = it,
                    style = typography.bodyMedium,
                    color = subTextColor(),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.size(30.dp))
            PrimaryButton(text = "Close") { onDismissClick() }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewErrorDialog() {
    DarkSurface {
        ErrorDialog(
            message = "Error playing video",
            summary = "Something wrong with the source"
        )
    }
}