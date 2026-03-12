package com.joshgm3z.triplerocktv.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    localDatastore: LocalDatastore,
    repository: MediaLocalRepository,
) : ViewModel() {
    private val _navDirectionState = MutableStateFlow<NavDirections?>(null)
    val navDirectionState = _navDirectionState.asStateFlow()

    init {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            _navDirectionState.value = when {
                localDatastore.getUserInfo() == null -> SplashScreenFragmentDirections.toLogin()
                repository.isContentEmpty() -> SplashScreenFragmentDirections.toLoading()
                else -> SplashScreenFragmentDirections.toBrowse()
            }
        }
    }
}