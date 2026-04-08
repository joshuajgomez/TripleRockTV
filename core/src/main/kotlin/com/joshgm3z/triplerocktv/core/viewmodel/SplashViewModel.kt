package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DestinationState {
    object Login : DestinationState()
    object Loading : DestinationState()
    object Home : DestinationState()
    class Error(val message: String) : DestinationState()
    class AccessDisabled(val message: String) : DestinationState()
    class AppUpdateNeeded(val message: String) : DestinationState()
}

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    localDatastore: LocalDatastore,
    repository: MediaLocalRepository,
    accessControlRepository: AccessControlRepository,
) : ViewModel() {
    private val _navDirectionState = MutableStateFlow<DestinationState?>(null)
    val navDirectionState = _navDirectionState.asStateFlow()

    init {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            val userInfo = localDatastore.getUserInfo()
            var accessState = accessControlRepository.getAccessState(userInfo?.username)
            var appUpdateState = accessControlRepository.appUpdateState()

            _navDirectionState.value = when {
                !accessState.enabled -> DestinationState.AccessDisabled(accessState.reason)
                !appUpdateState.enabled -> DestinationState.AppUpdateNeeded(
                    appUpdateState.reason
                )

                userInfo == null -> DestinationState.Login
                repository.isContentEmpty() -> DestinationState.Loading
                else -> DestinationState.Home
            }
        }
    }
}