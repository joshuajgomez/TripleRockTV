package com.joshgm3z.triplerocktv.ui.login

import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet

fun getLoginConstraints() = ConstraintSet {
    val logo = createRefFor(LoginLayoutId.Logo)
    val title = createRefFor(LoginLayoutId.Title)
    val urlInput = createRefFor(LoginLayoutId.UrlInput)
    val usernameInput = createRefFor(LoginLayoutId.UsernameInput)
    val passwordInput = createRefFor(LoginLayoutId.PasswordInput)
    val submitButton = createRefFor(LoginLayoutId.SubmitButton)
    val errorMessage = createRefFor(LoginLayoutId.ErrorMessage)

    constrain(logo) {
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
    }
    constrain(errorMessage) {
        top.linkTo(submitButton.top)
        bottom.linkTo(submitButton.bottom)
        end.linkTo(submitButton.start, 20.dp)
    }
    constrain(urlInput) {
        top.linkTo(parent.top)
        end.linkTo(parent.end)
    }
    constrain(usernameInput) {
        top.linkTo(urlInput.bottom)
        end.linkTo(parent.end)
    }
    constrain(passwordInput) {
        top.linkTo(usernameInput.bottom)
        end.linkTo(parent.end)
    }
    constrain(submitButton) {
        bottom.linkTo(parent.bottom)
        end.linkTo(parent.end)
    }
}