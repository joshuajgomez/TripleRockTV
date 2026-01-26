package com.joshgm3z.triplerocktv.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun getLoginViewModel(): ILoginViewModel = when {
    LocalInspectionMode.current -> FakeLoginViewModel()
    else -> hiltViewModel<LoginViewModel>()
}

@Composable
fun LoginScreen(
    viewModel: ILoginViewModel = getLoginViewModel(),
    onLoginSuccess: () -> Unit = {},
) {
    val uiState = viewModel.uiState.collectAsState().value

    if (uiState.loginSuccess) {
        onLoginSuccess()
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoginForm(uiState) { webUrl, username, password ->
            viewModel.onLoginClick(webUrl, username, password)
        }
    }
}

enum class LoginLayoutId {
    Logo,
    Title,
    ErrorMessage,
    UrlInput,
    UsernameInput,
    PasswordInput,
    SubmitButton,
}

@Composable
fun LoginForm(
    uiState: LoginUiState,
    onLoginClick: (
        webUrl: String,
        username: String,
        password: String,
    ) -> Unit = { _, _, _ -> }
) {
    var webUrl by remember { mutableStateOf("https://") }
    var webUrlError by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    ConstraintLayout(
        constraintSet = getLoginConstraints(),
        modifier = Modifier
            .width(600.dp)
            .height(320.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.logo_3rocktv_cutout),
            contentDescription = null,
            modifier = Modifier
                .layoutId(LoginLayoutId.Logo)
                .width(200.dp)
        )
        ErrorCard(
            modifier = Modifier.layoutId(LoginLayoutId.ErrorMessage),
            message = uiState.errorMessage
        )
        TextInput(
            modifier = Modifier.layoutId(LoginLayoutId.UrlInput),
            text = webUrl,
            label = "Server URL",
            isError = webUrlError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            enabled = !uiState.loading,
        ) { webUrl = it }
        TextInput(
            modifier = Modifier.layoutId(LoginLayoutId.UsernameInput),
            text = username,
            label = "Username",
            isError = usernameError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            enabled = !uiState.loading
        ) { username = it }
        TextInput(
            modifier = Modifier.layoutId(LoginLayoutId.PasswordInput),
            text = password,
            label = "Password",
            isError = passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            enabled = !uiState.loading,
        ) { password = it }
        SubmitButton(
            onClick = {
                webUrlError = webUrl.isEmpty()
                usernameError = username.isEmpty()
                passwordError = password.isEmpty()

                if (webUrl.isNotEmpty()
                    && username.isNotEmpty()
                    && password.isNotEmpty()
                ) onLoginClick(
                    webUrl,
                    username,
                    password
                )
            },
            isLoading = uiState.loading,
            modifier = Modifier.layoutId(LoginLayoutId.SubmitButton)
        )
    }
}

@Composable
fun SubmitButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick() },
        modifier = modifier.width(100.dp),
        enabled = !isLoading,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colorScheme.onBackground
            )
            else Text("Sign in")
        }
    }
}

@TvPreview
@Composable
private fun PreviewLoginScreen_Initial() {
    TripleRockTVTheme {
        LoginScreen()
    }
}

@TvPreview
@Composable
private fun PreviewLoginScreen_Loading() {
    TripleRockTVTheme {
        LoginScreen(viewModel = FakeLoginViewModel(LoginUiState(loading = true)))
    }
}

@TvPreview
@Composable
private fun PreviewLoginScreen_Error() {
    TripleRockTVTheme {
        LoginScreen(
            viewModel = FakeLoginViewModel(
                LoginUiState(errorMessage = "Unable to connect to internet")
            )
        )
    }
}