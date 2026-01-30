package com.joshgm3z.triplerocktv.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    var loading: Boolean = false,
    var errorMessage: String? = null,
    var loginSuccess: Boolean = false,
)

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val repository: LoginRepository
) : ViewModel(), ILoginViewModel {
    private val _uiState = MutableStateFlow(LoginUiState())
    override val uiState = _uiState.asStateFlow()

    override fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
        _uiState.update {
            it.copy(
                loading = true,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = webUrl,
                username = username,
                password = password,
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            loginSuccess = true,
                            loading = false
                        )
                    }
                },
                onError = { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error,
                            loading = false
                        )
                    }
                },
            )
        }
    }

    override fun onLogoutClick(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            repository.tryLogout {
                onLogoutComplete()
            }
        }
    }
}