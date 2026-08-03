package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    showBackground: Boolean = false,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (showBackground) colorScheme.background
                else Color.Transparent
            )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null,
            tint = colorScheme.onBackground
        )
    }
}

@DarkPreview
@Composable
private fun PreviewCloseButton() {
    DarkSurface {
        CloseButton()
    }
}

@DarkPreview
@Composable
private fun PreviewCloseButton_showBackground() {
    DarkSurface {
        CloseButton(showBackground = true)
    }
}