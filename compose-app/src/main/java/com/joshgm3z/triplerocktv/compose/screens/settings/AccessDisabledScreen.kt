package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.compose.screens.common.ButtonItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.InfoWithButtons

@Composable
fun AccessDisabledScreen(
    title: String = "Access disabled",
    message: String,
    onExitClicked: () -> Unit = {},
) {
    InfoWithButtons(
        title = title,
        message = message,
        buttons = listOf(
            ButtonItem(
                primary = true,
                text = "Exit app",
                onClick = onExitClicked
            )
        ),
    )
}

@DarkPreview
@Composable
fun PreviewAccessDisabledScreen() {
    DarkSurface {
        AccessDisabledScreen(
            title = "Access disabled",
            message = "The user jgomez is banned from using this app"
        )
    }
}