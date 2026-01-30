package com.joshgm3z.triplerocktv.ui.login

import com.joshgm3z.triplerocktv.ui.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel(
    uiState: LoginUiState = LoginUiState()
) : ILoginViewModel {
    override val uiState: StateFlow<LoginUiState> = MutableStateFlow(uiState)
    override val userInfo: StateFlow<UserInfo?> = MutableStateFlow(
        UserInfo(
            username = "someone112",
            password = "password",
            webUrl = "https:://www.xtream.com",
            expiryDate = "1329871"
        )
    )

    override fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
    }

    override fun onLogoutClick(onLogoutComplete: () -> Unit) {}
}
