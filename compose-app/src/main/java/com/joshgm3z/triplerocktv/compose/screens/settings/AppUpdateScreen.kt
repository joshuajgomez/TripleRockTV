package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.ButtonItem
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.InfoWithButtons
import com.joshgm3z.triplerocktv.core.viewmodel.ButtonAction
import com.joshgm3z.triplerocktv.core.viewmodel.SelfUpdateUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SelfUpdateViewModel

@Composable
fun AppUpdateScreen(
    viewModel: SelfUpdateViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    viewModel.uiState.collectAsState().value.let {
        AppUpdateScreenContent(
            uiState = it,
            onDismissClick = onBackClick,
            onActionClick = { viewModel.onButtonClick() },
        )
    }
}

@Composable
private fun AppUpdateScreenContent(
    uiState: SelfUpdateUiState,
    onDismissClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    InfoWithButtons(
        title = uiState.title,
        message = uiState.subtitle,
        buttons = listOf(
            ButtonItem(
                primary = true,
                loading = !uiState.enableButtons,
                text = uiState.buttonAction.text,
                onClick = onActionClick,
                enabled = uiState.enableButtons
            ),
            ButtonItem(
                text = "Dismiss",
                onClick = onDismissClick,
                enabled = uiState.enableButtons
            )
        ),
    )
}

@DarkPreview
@Composable
private fun PreviewAppUpdateScreen_UpdateNow() {
    DarkSurface {
        AppUpdateScreenContent(
            SelfUpdateUiState(
                title = "Update available",
                subtitle = "New version 1.0.0 is available for download",
                enableButtons = true,
                buttonAction = ButtonAction.UpdateNow
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewAppUpdateScreen_CheckingUpdates() {
    DarkSurface {
        AppUpdateScreenContent(
            SelfUpdateUiState(
                title = "Checking updates",
                enableButtons = false,
                buttonAction = ButtonAction.UpdateNow
            )
        )
    }
}