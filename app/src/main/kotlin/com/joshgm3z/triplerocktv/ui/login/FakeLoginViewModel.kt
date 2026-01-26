package com.joshgm3z.triplerocktv.ui.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel(
    uiState: LoginUiState = LoginUiState()
) : ILoginViewModel {
    override val uiState: StateFlow<LoginUiState> = MutableStateFlow(uiState)
    override fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
    }
}
