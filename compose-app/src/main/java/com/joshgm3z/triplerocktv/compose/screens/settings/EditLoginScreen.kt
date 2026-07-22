package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.util.orIfDebug
import com.joshgm3z.triplerocktv.core.viewmodel.LoginUiState
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel

@Composable
fun EditLoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    EditLoginScreenContent(
        uiState = uiState,
        onSubmitClicked = { serverUrl, username, password ->
            viewModel.onLoginClick(
                serverUrl,
                username,
                password
            )
        }, onBackClick = {
            onBackClick()
        })
}

@Composable
private fun EditLoginScreenContent(
    uiState: LoginUiState,
    onSubmitClicked: (
        serverUrl: String,
        username: String,
        password: String
    ) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val status = when {
        uiState.loginSuccess -> "Login details updated"
        uiState.errorMessage != null -> uiState.errorMessage
        else -> null
    }
    var serverUrl by remember { mutableStateOf("https://".orIfDebug(Secrets.webUrl)) }
    var username by remember { mutableStateOf("".orIfDebug(Secrets.username)) }
    var password by remember { mutableStateOf("".orIfDebug(Secrets.password)) }

    SettingScaffold(
        title = "Edit login details",
        onBackClick = onBackClick
    ) {
        TextField(
            enabled = !uiState.loading,
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            enabled = !uiState.loading,
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()

        )

        TextField(
            enabled = !uiState.loading,
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()

        )
        AnimatedVisibility(status != null) {
            Text(status ?: "")
        }

        Spacer(
            Modifier
                .fillMaxSize()
                .weight(1f)
        )
        PrimaryButton(
            loading = uiState.loading,
            onClick = {
                onSubmitClicked(
                    serverUrl,
                    username,
                    password
                )
            },
            modifier = Modifier.fillMaxWidth(),
            text = "Submit"
        )
    }
}

@DarkPreview
@Composable
private fun PreviewEditLoginScreen() {
    TripleRockTvTheme {
        EditLoginScreenContent(LoginUiState())
    }
}