package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.orIfDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    class Initial(val username: String, val password: String, val webUrl: String) : LoginUiState()
    object Loading : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    object LoginSuccess : LoginUiState()
}

data class UserInfo(
    val username: String,
    val password: String,
    val webUrl: String,
    val expiryDate: String,
    val sessionId: String,
)

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val repository: LoginRepository,
    private val localDatastore: LocalDatastore,
    private val firebaseLogger: FirebaseLogger,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userInfo = localDatastore.getUserInfo()
            _uiState.value = LoginUiState.Initial(
                username = userInfo?.username ?: "".orIfDebug(Secrets.username),
                password = userInfo?.password ?: "".orIfDebug(Secrets.password),
                webUrl = userInfo?.webUrl ?: "http://".orIfDebug(Secrets.webUrl)
            )
        }
    }

    fun onLoginClick(
        webUrl: String,
        username: String,
        password: String
    ) {
        Logger.entry()
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = webUrl,
                username = username,
                password = password,
                onSuccess = {
                    Logger.debug("Login successful")
                    firebaseLogger.logUserLogin(username)
                    Firebase.analytics.setUserId(username)
                    Firebase.crashlytics.setUserId(username)
                    _uiState.value = LoginUiState.LoginSuccess
                },
                onError = { error ->
                    Logger.warn("Login failed")
                    firebaseLogger.logUserLoginFail(username)
                    _uiState.value = LoginUiState.Error(error)
                },
            )
        }
    }
}