package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.SecondaryButton
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel

@Composable
fun LogoutScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLogoutComplete: () -> Unit = {},
) {
    var loading by remember { mutableStateOf(false) }
    SettingScaffold(
        title = "Logout from app?",
        onBackClick = onBackClick
    ) {
        Text("Logging out will delete all downloaded data")
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
        PrimaryButton(
            text = "Logout",
            loading = loading,
            enabled = !loading
        ) {
            loading = true
            viewModel.onLogoutClick {
                onLogoutComplete()
            }
        }
        SecondaryButton(
            text = "Dismiss",
            enabled = !loading
        ) {
            onBackClick()
        }
    }
}

@DarkPreview
@Composable
private fun PreviewLogoutScreen() {
    DarkSurface {
        LogoutScreen()
    }
}