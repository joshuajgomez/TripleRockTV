package com.joshgm3z.triplerocktv.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface ILoginViewModel {
    val uiState: StateFlow<LoginUiState>
    fun onLoginClick(
        username: String,
        password: String
    )
}
