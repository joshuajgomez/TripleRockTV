package com.joshgm3z.triplerocktv.ui.login

import kotlinx.coroutines.flow.StateFlow

interface ILoginViewModel {
    val uiState: StateFlow<LoginUiState>
    val userInfo: StateFlow<UserInfo?>
    fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    )

    fun onLogoutClick(onLogoutComplete: () -> Unit = {})
}
