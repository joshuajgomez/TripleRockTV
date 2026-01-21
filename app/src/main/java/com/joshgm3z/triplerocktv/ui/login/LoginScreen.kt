package com.joshgm3z.triplerocktv.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.LoginViewModel

enum class LoginLayoutId {
    Logo,
    UsernameInput,
    PasswordInput,
    SubmitButton,
    ErrorMessage
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState = viewModel.uiState.collectAsState().value

    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
        constraintSet = getLoginConstraints()
    ) {
        Image(
            painter = painterResource(R.drawable.avatar_movie),
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
                viewModel.onLoginClick(
                    username = username,
                    password = password
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
            errorMessage!!,
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
        modifier = modifier,
        enabled = !isLoading
    ) {
        AnimatedVisibility(isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.size(10.dp))
        }
        Text("Login")
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
        Text(label)
        TextField(
            value = text,
            placeholder = { Text("") },
            onValueChange = { onTextChange(it) },
            enabled = enabled
        )
    }
}

private fun getLoginConstraints() = ConstraintSet {
    val logo = createRefFor(LoginLayoutId.Logo)
    val usernameInput = createRefFor(LoginLayoutId.UsernameInput)
    val passwordInput = createRefFor(LoginLayoutId.PasswordInput)
    val submitButton = createRefFor(LoginLayoutId.SubmitButton)
    val errorMessage = createRefFor(LoginLayoutId.ErrorMessage)

    constrain(logo) {
        top.linkTo(parent.top, 50.dp)
        end.linkTo(parent.end, 50.dp)
    }
    constrain(usernameInput) {
        top.linkTo(logo.bottom, 30.dp)
        start.linkTo(logo.start)
        end.linkTo(logo.end)
    }
    constrain(passwordInput) {
        top.linkTo(usernameInput.bottom, 10.dp)
        start.linkTo(logo.start)
        end.linkTo(logo.end)
    }
    constrain(submitButton) {
        top.linkTo(passwordInput.bottom, 10.dp)
        start.linkTo(logo.start)
        end.linkTo(logo.end)
    }
    constrain(errorMessage) {
        top.linkTo(submitButton.bottom, 10.dp)
        start.linkTo(logo.start)
        end.linkTo(logo.end)
    }
}

@TvPreview
@Composable
private fun PreviewLoginScreen() {
    TripleRockTVTheme {
        LoginScreen()
    }
}