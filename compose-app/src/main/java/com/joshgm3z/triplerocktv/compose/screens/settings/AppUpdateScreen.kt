package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.SecondaryButton
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
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
    SettingScaffold(
        "App update",
        onBackClick = onDismissClick
    ) {
        UpdateContent(
            uiState = uiState,
            onDismissClick = onDismissClick,
            onActionClick = onActionClick
        )
    }
}

@Composable
fun ColumnScope.UpdateContent(
    uiState: SelfUpdateUiState,
    onDismissClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    Spacer(Modifier.size(20.dp))
    Text(
        text = uiState.title,
        style = typography.headlineLarge,
        color = textColor(),
    )
    Spacer(Modifier.size(10.dp))
    Text(
        text = uiState.subtitle ?: "",
        style = typography.bodyLarge,
        color = subTextColor(),
    )
    AnimatedVisibility(
        visible = !uiState.enableButtons,
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        CircularProgressIndicator()
    }
    Spacer(
        Modifier
            .fillMaxSize()
            .weight(1f)
    )
    PrimaryButton(
        onClick = onActionClick,
        enabled = uiState.enableButtons,
        modifier = Modifier.fillMaxWidth(),
        text = uiState.buttonAction.text
    )
    Spacer(Modifier.size(5.dp))
    SecondaryButton(
        onClick = onDismissClick,
        enabled = uiState.enableButtons,
        modifier = Modifier.fillMaxWidth(),
        text = "Dismiss"
    )
}

@DarkPreview
@Composable
private fun PreviewAppUpdateScreen() {
    DarkSurface {
        AppUpdateScreenContent(
            SelfUpdateUiState(
                title = "Update available",
                subtitle = "New version 1.0.0 is available for download",
                enableButtons = false,
                buttonAction = ButtonAction.UpdateNow
            )
        )
    }
}