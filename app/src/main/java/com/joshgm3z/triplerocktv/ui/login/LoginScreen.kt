package com.joshgm3z.triplerocktv.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.ILoginViewModel
import com.joshgm3z.triplerocktv.viewmodel.LoginUiState
import com.joshgm3z.triplerocktv.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow

enum class LoginLayoutId {
    Logo,
    UsernameInput,
    PasswordInput,
    SubmitButton,
    ErrorMessage
}

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
        Image(
            painter = painterResource(R.drawable.avatar_movie),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorScheme.background.copy(alpha = 0.95f))
        ) {}
        LoginForm(uiState) { username, password ->
            viewModel.onLoginClick(username, password)
        }
    }
}

@Composable
fun LoginForm(
    uiState: LoginUiState,
    onLoginClick: (username: String, password: String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .width(250.dp)
            .height(400.dp)
            .background(color = colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp),

        ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Image(
            painter = painterResource(R.drawable.logo_3rocktv_cutout),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .layoutId(LoginLayoutId.Logo)
        )
        TextInput(
            modifier = Modifier.layoutId(LoginLayoutId.UsernameInput),
            text = username,
            label = "Enter username",
            enabled = !uiState.loading
        ) { username = it }
        TextInput(
            modifier = Modifier.layoutId(LoginLayoutId.PasswordInput),
            text = password,
            label = "Enter password",
            enabled = !uiState.loading,
        ) { password = it }
        SubmitButton(
            onClick = {
                onLoginClick(
                    username,
                    password
                )
            },
            isLoading = uiState.loading,
            modifier = Modifier.layoutId(LoginLayoutId.SubmitButton)
        )
        ErrorMessage(
            modifier = Modifier.layoutId(LoginLayoutId.ErrorMessage),
            errorMessage = uiState.errorMessage
        )
    }
}

@Composable
fun ErrorMessage(
    modifier: Modifier,
    errorMessage: String?
) {
    AnimatedVisibility(
        !errorMessage.isNullOrEmpty(),
        modifier = modifier
    ) {
        Text(
            errorMessage ?: "",
            color = colorScheme.onErrorContainer,
            modifier = Modifier.background(color = colorScheme.errorContainer)
        )
    }
}

@Composable
fun SubmitButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = { onClick() },
        modifier = modifier.fillMaxWidth(),
        enabled = !isLoading,
        colors = ButtonDefaults.textButtonColors(
            containerColor = colorScheme.primaryContainer,
            disabledContainerColor = colorScheme.primaryContainer.copy(alpha = 0.5f),
            contentColor = colorScheme.onPrimaryContainer,
            disabledContentColor = colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
        ),
    ) {
        AnimatedVisibility(isLoading) {
            Row {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
            }
        }
        Text(if (isLoading) "Logging in..." else "Login")
    }
}

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    enabled: Boolean,
    onTextChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, color = colorScheme.onBackground)
        Spacer(Modifier.size(10.dp))
        TextField(
            value = text,
            placeholder = { Text("") },
            onValueChange = { onTextChange(it) },
            enabled = enabled
        )
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
        LoginScreen(viewModel = FakeLoginViewModel(MutableStateFlow(LoginUiState(loading = true))))
    }
}

@TvPreview
@Composable
private fun PreviewLoginScreen_Error() {
    TripleRockTVTheme {
        LoginScreen(viewModel = FakeLoginViewModel(MutableStateFlow(LoginUiState(errorMessage = "Unable to connect to internet"))))
    }
}