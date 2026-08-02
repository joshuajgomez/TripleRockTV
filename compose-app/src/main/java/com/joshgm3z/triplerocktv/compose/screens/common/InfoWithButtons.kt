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

data class ButtonItem(
    val primary: Boolean = false,
    val text: String,
    val onClick: () -> Unit = {},
    val enabled: Boolean = true,
    val loading: Boolean = false,
)

@Composable
fun InfoWithButtons(
    title: String,
    message: String? = null,
    buttons: List<ButtonItem> = emptyList()
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
            style = typography.headlineLarge,
            color = colorScheme.onBackground
        )
        Spacer(Modifier.size(10.dp))
        message?.let {
            Text(
                text = it,
                style = typography.bodyLarge,
                color = colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        Spacer(
            Modifier
                .fillMaxSize()
                .weight(1f)
        )
        buttons.forEach {
            if (it.primary) {
                PrimaryButton(
                    text = it.text,
                    onClick = it.onClick,
                    enabled = it.enabled,
                    loading = it.loading
                )
            } else {
                SecondaryButton(
                    text = it.text,
                    onClick = it.onClick,
                    enabled = it.enabled,
                    loading = it.loading
                )
            }
        }
    }
}

@DarkPreview
@Composable
fun PreviewInfoWithButtons() {
    DarkSurface {
        InfoWithButtons(
            title = "Some major info",
            message = "More information about the major info",
            buttons = listOf(
                ButtonItem(
                    primary = true,
                    text = "Okay"
                ),
                ButtonItem(
                    text = "Contact support"
                )
            )
        )
    }
}