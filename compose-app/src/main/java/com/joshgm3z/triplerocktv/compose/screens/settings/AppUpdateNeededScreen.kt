package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.compose.screens.common.ButtonItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.InfoWithButtons
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme

@Composable
fun AppUpdateNeededScreen(
    title: String = "App update needed",
    message: String,
    onExitClicked: () -> Unit = {},
    navigateToAppUpdate: () -> Unit = {},
) {
    InfoWithButtons(
        title = title,
        message = message,
        buttons = listOf(
            ButtonItem(
                primary = true,
                text = "Check updates",
                onClick = navigateToAppUpdate
            ),
            ButtonItem(
                text = "Exit app",
                onClick = onExitClicked
            )
        )
    )
}

@DarkPreview
@Composable
fun PreviewAppUpdateNeededScreen() {
    DarkSurface {
        AppUpdateNeededScreen(
            message = "Update to v234 to continue using the app"
        )
    }
}