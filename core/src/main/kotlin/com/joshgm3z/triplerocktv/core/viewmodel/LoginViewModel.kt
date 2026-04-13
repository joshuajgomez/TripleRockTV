package com.joshgm3z.triplerocktv.core.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.joshgm3z.triplerocktv.core.R
import com.joshgm3z.triplerocktv.core.repository.CompanionLoginRepository
import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.getQrCode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val companionLoginRepository: CompanionLoginRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _qrCodeUiState = MutableStateFlow<Bitmap?>(null)
    val qrCodeUiState = _qrCodeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            companionLoginRepository.newLoginSessionId()?.let {
                val appUrl = context.getString(R.string.online_typer_app_url)
                _qrCodeUiState.value = "$appUrl?id=$it".getQrCode()
            }
        }
    }

    fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
        Logger.entry()
        _uiState.update {
            it.copy(loading = true, errorMessage = null, loginSuccess = false)
        }
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = webUrl,
                username = username,
                password = password,
                onSuccess = {
                    Logger.debug("Login successful")
                    FirebaseLogger.logUserLogin(username)
                    Firebase.analytics.setUserId(username)
                    Firebase.crashlytics.setUserId(username)
                    _uiState.update {
                        it.copy(loginSuccess = true, loading = false)
                    }
                },
                onError = { error ->
                    Logger.warn("Login failed")
                    FirebaseLogger.logUserLoginFail(username)
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