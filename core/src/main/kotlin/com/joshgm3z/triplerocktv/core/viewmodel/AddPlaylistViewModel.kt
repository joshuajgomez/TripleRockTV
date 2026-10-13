package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.m3u8.M3u8PlaylistRepository
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistType
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.orIfDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddPlaylistUiState(val buttonText: String) {
    class Initial(
        val username: String,
        val password: String,
        val webUrl: String
    ) : AddPlaylistUiState("Sign In")

    object Loading : AddPlaylistUiState("Verifying")
    class Error(val message: String) : AddPlaylistUiState("Sign in")
    object LoginSuccess : AddPlaylistUiState("Verified")
}

@HiltViewModel
class AddPlaylistViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LoginRepository,
    private val m3u8PlaylistRepository: M3u8PlaylistRepository,
    private val firebaseLogger: FirebaseLogger,
) : ViewModel() {

    private val playlistType = savedStateHandle.get<PlaylistType>("playlistType")
        ?: throw IllegalArgumentException("playlistType is required")

    private val _uiState =
        MutableStateFlow<AddPlaylistUiState>(AddPlaylistUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val defaultUrl = if (playlistType == PlaylistType.Xtream) Secrets.xtreamUrl
            else Secrets.m3u8Url

            _uiState.value = AddPlaylistUiState.Initial(
                username = "".orIfDebug(Secrets.username),
                password = "".orIfDebug(Secrets.password),
                webUrl = "http://".orIfDebug(defaultUrl)
            )
        }
    }

    fun verifyXtreamLogin(
        webUrl: String,
        username: String,
        password: String
    ) {
        Logger.entry()
        _uiState.value = AddPlaylistUiState.Loading
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
        _uiState.value = AddPlaylistUiState.Loading
        viewModelScope.launch {
            delay(1000)
            val valid = m3u8PlaylistRepository.isUrlValid(webUrl)
            _uiState.value = if (valid) {
                Logger.debug("M3u8 URL verified")
                firebaseLogger.logM3u8UrlVerification(webUrl)
                AddPlaylistUiState.LoginSuccess
            } else {
                Logger.warn("M3u8 URL verification failed")
                AddPlaylistUiState.Error("Invalid url")
            }
        }
    }
}