package com.joshgm3z.triplerocktv.ui.login

import com.joshgm3z.triplerocktv.viewmodel.ILoginViewModel
import com.joshgm3z.triplerocktv.viewmodel.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel(
    override val uiState: StateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
) : ILoginViewModel {
    override fun onLoginClick(username: String, password: String) {}
}
