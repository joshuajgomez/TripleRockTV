package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
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
        button1 = "Check updates",
        button2 = "Exit app",
        onButton1Clicked = { navigateToAppUpdate() },
        onButton2Clicked = onExitClicked
    )
}

@DarkPreview
@Composable
fun PreviewAppUpdateNeededScreen() {
    TripleRockTvTheme {
        AppUpdateNeededScreen(
            message = "Update to v234 to continue using the app"
        )
    }
}