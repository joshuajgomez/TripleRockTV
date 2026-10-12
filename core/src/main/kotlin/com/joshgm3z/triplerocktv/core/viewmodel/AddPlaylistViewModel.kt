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

sealed class AddPlaylistUiState {
    class Initial(val username: String, val password: String, val webUrl: String) :
        AddPlaylistUiState()

    class Loading(val message: String) : AddPlaylistUiState()
    data class Error(val message: String) : AddPlaylistUiState()
    object LoginSuccess : AddPlaylistUiState()
}

data class UserInfo2(
    val username: String,
    val password: String,
    val webUrl: String,
    val expiryDate: String,
    val sessionId: String,
)

@HiltViewModel
class AddPlaylistViewModel
@Inject constructor(
    private val repository: LoginRepository,
    private val localDatastore: LocalDatastore,
    private val firebaseLogger: FirebaseLogger,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<AddPlaylistUiState>(AddPlaylistUiState.Loading("Sign in"))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userInfo = localDatastore.getUserInfo()
            _uiState.value = AddPlaylistUiState.Initial(
                username = userInfo?.username ?: "".orIfDebug(Secrets.username),
                password = userInfo?.password ?: "".orIfDebug(Secrets.password),
                webUrl = userInfo?.webUrl ?: "http://".orIfDebug(Secrets.webUrl)
            )
        }
    }

    fun verifyXtreamLogin(
        webUrl: String,
        username: String,
        password: String
    ) {
        Logger.entry()
        _uiState.value = AddPlaylistUiState.Loading("Signing in")
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
                    _uiState.value = AddPlaylistUiState.LoginSuccess
                },
                onError = { error ->
                    Logger.warn("Login failed")
                    firebaseLogger.logUserLoginFail(username)
                    _uiState.value = AddPlaylistUiState.Error(error)
                },
            )
        }
    }

    fun verifyM3u8Url(webUrl: String) {
        Logger.entry()
        _uiState.value = AddPlaylistUiState.Loading("Verifying")
        viewModelScope.launch {
            repository.verifyM3u8Url(
                webUrl = webUrl,
                onSuccess = {
                    Logger.debug("M3u8 URL verified")
                    firebaseLogger.logM3u8UrlVerification(webUrl)
                    _uiState.value = AddPlaylistUiState.LoginSuccess
                },
                onError = { error ->
                    Logger.warn("M3u8 URL verification failed")
                    _uiState.value = AddPlaylistUiState.Error(error)
                },
            )
        }
    }
}