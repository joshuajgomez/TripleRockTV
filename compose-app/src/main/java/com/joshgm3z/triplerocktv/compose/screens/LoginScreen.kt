package com.joshgm3z.triplerocktv.compose.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.R
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.appBottomPadding
import com.joshgm3z.triplerocktv.compose.screens.common.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import com.joshgm3z.triplerocktv.core.util.orIfDebug

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    viewModel.uiState.collectAsState().value.let { uiState ->
        LoginScreenContent(
            onSubmitClicked = { serverUrl, username, password ->
                viewModel.onLoginClick(
                    serverUrl,
                    username,
                    password
                )
            },
            loading = uiState.loading,
            status = when {
                uiState.loginSuccess -> "Login successful!"
                uiState.errorMessage != null -> uiState.errorMessage
                else -> null
            }
        )
        LaunchedEffect(uiState.loginSuccess) {
            if (uiState.loginSuccess) {
                onLoginSuccess()
            }
        }
    }
}

@Composable
private fun LoginScreenContent(
    onSubmitClicked: (
        serverUrl: String,
        username: String,
        password: String
    ) -> Unit = { _, _, _ -> },
    loading: Boolean = false,
    status: String? = null,
) {
    var serverUrl by remember { mutableStateOf("https://".orIfDebug(Secrets.webUrl)) }
    var username by remember { mutableStateOf("".orIfDebug(Secrets.username)) }
    var password by remember { mutableStateOf("".orIfDebug(Secrets.password)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = appHorizontalPadding,
                end = appHorizontalPadding,
                bottom = appBottomPadding(),
            )
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical,
            ),
    ) {
//        Logo()
        OutlinedTextField(
            enabled = !loading,
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(10.dp))

        OutlinedTextField(
            enabled = !loading,
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.size(10.dp))

        OutlinedTextField(
            enabled = !loading,
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.size(20.dp))

        PrimaryButton(
            text = "Submit",
            loading = loading,
            onClick = {
                onSubmitClicked(
                    serverUrl,
                    username,
                    password
                )
            },
        )

        AnimatedVisibility(
            status != null,
        ) {
            Text(status ?: "")
        }
    }
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@DarkPreview
@Composable
private fun PreviewLoginScreen() {
    TripleRockTvTheme {
        LoginScreenContent()
    }
}
