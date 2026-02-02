package com.joshgm3z.triplerocktv.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
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
    private val localDatastore: LocalDatastore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    init {
        viewModelScope.launch {
            localDatastore.getLoginCredentials {
                _userInfo.value = it
            }
        }
    }

    fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
        _uiState.update {
            it.copy(loading = true, errorMessage = null)
        }
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = webUrl,
                username = username,
                password = password,
                onSuccess = {
                    _uiState.update {
                        it.copy(loginSuccess = true, loading = false)
                    }
                },
                onError = { error ->
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
        viewModelScope.launch {
            repository.tryLogout {
                onLogoutComplete()
            }
        }
    }
}