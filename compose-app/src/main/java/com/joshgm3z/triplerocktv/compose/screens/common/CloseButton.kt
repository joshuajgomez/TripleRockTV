package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(color = colorScheme.onBackground.copy(alpha = 0.1f))
            .size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = colorScheme.onBackground
        )
    }
}