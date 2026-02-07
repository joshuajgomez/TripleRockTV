package com.joshgm3z.triplerocktv.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

data class UserInfo(
    val username: String,
    val password: String,
    val webUrl: String,
    val expiryDate: String,
    val lastContentUpdate: String,
)

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val repository: LoginRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
        Logger.entry()
        _uiState.update {
            it.copy(loading = true, errorMessage = null)
        }
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = webUrl,
                username = username,
                password = password,
                onSuccess = {
                    Logger.debug("Login successful")
                    _uiState.update {
                        it.copy(loginSuccess = true, loading = false)
                    }
                },
                onError = { error ->
                    Logger.warn("Login failed")
                    _uiState.update {
                        it.copy(errorMessage = error, loading = false)
                    }
                },
            )
        }
    }

    fun onLogoutClick(onLogoutComplete: () -> Unit) {
        _uiState.update {
            it.copy(loading = true, errorMessage = null)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.tryLogout {
                viewModelScope.launch(Dispatchers.Main) {
                    onLogoutComplete()
                }
            }
        }
    }
}