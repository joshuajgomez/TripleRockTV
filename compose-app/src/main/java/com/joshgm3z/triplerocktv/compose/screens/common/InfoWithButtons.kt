package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.settings.appBottomPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appTopPadding

@Composable
fun InfoWithButtons(
    title: String,
    message: String = "",
    button1: String? = null,
    button2: String? = null,
    onButton1Clicked: () -> Unit = {},
    onButton2Clicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = appHorizontalPadding,
                end = appHorizontalPadding,
                top = appTopPadding,
                bottom = appBottomPadding,
            )
    ) {
        Text(
            text = title,
            style = typography.titleLarge,
            color = colorScheme.onBackground
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Spacer(
            Modifier
                .fillMaxSize()
                .weight(1f)
        )
        button1?.let {
            PrimaryButton(text = it, onClick = onButton1Clicked)
        }
        button2?.let {
            SecondaryButton(text = it, onClick = onButton2Clicked)
        }
    }
}

@DarkPreview
@Composable
fun PreviewInfoWithButtons() {
    DarkSurface {
        InfoWithButtons(
            title = "Access disabled",
            message = "The user jgomez is banned from using this app",
            button1 = "Exit",
            button2 = "Contact support"
        )
    }
}