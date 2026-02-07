package com.joshgm3z.triplerocktv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.ui.login.UserInfo
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CredentialUiState(
    var loading: Boolean = false,
    var errorMessage: String? = null,
    var verificationSuccess: Boolean = false,
    var userInfo: UserInfo? = null,
)

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    localDatastore: LocalDatastore,
    private val repository: LoginRepository,
) : ViewModel() {
    private val _credentialState = MutableStateFlow(CredentialUiState())
    val credentialState = _credentialState.asStateFlow()

    init {
        Logger.entry()
        viewModelScope.launch {
            localDatastore.getUserInfo()?.let { userInfo ->
                _credentialState.update { it.copy(userInfo = userInfo) }
            }
        }
    }

    fun verifyCredentials(serverUrl: String, username: String, password: String) {
        Logger.entry()
        _credentialState.update {
            it.copy(loading = true, errorMessage = null)
        }
        viewModelScope.launch {
            repository.tryLogin(
                webUrl = serverUrl,
                username = username,
                password = password,
                onSuccess = {
                    Logger.debug("Verification successful")
                    _credentialState.update {
                        it.copy(verificationSuccess = true, loading = false)
                    }
                },
                onError = { error ->
                    Logger.warn("Verification failed")
                    _credentialState.update {
                        it.copy(errorMessage = error, loading = false)
                    }
                },
            )
        }
    }

}