package com.joshgm3z.triplerocktv.ui.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel(
    override val uiState: StateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
) : ILoginViewModel {
    override fun onLoginClick(username: String, password: String) {}
}
