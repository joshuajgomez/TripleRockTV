package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface

val appHorizontalPadding = 20.dp
val appBottomPadding = 40.dp
val appTopPadding = 70.dp

@Composable
fun SettingScaffold(
    title: String,
    onBackClick: () -> Unit = {},
    applyBottomPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = appHorizontalPadding,
                end = appHorizontalPadding,
                top = appTopPadding,
                bottom = if (applyBottomPadding) appBottomPadding else 0.dp,
            )
    ) {
        AppHeader(title, onBackClick)
        Spacer(Modifier.size(10.dp))
        content()
    }
}

@Composable
private fun AppHeader(
    title: String,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .background(
                    color = colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.size(20.dp))
        Text(
            text = title,
            style = typography.headlineSmall,
            color = colorScheme.onBackground
        )
    }
}

@DarkPreview
@Composable
private fun PreviewSettingScaffold() {
    DarkSurface {
        SettingScaffold(
            title = "Settings",
            onBackClick = {}
        ) {
            Text(
                "Content start",
                color = colorScheme.onBackground
            )
            Spacer(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
            Text(
                "Content end",
                color = colorScheme.onBackground
            )
        }
    }
}