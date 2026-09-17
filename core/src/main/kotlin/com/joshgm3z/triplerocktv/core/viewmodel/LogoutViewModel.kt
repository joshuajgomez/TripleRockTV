package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel
@Inject constructor(
    private val repository: LoginRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onLogoutClick(onLogoutComplete: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.tryLogout {
                viewModelScope.launch(Dispatchers.Main) {
                    onLogoutComplete()
                }
            }
        }
    }
}