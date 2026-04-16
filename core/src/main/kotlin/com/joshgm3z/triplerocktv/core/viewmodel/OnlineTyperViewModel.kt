package com.joshgm3z.triplerocktv.core.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.R
import com.joshgm3z.triplerocktv.core.repository.OnlineTyperRepository
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.getQrCode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineTyperViewModel
@Inject constructor(
    private val repository: OnlineTyperRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _qrCodeBitmapState = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmapState = _qrCodeBitmapState.asStateFlow()

    val inputTextFlow: StateFlow<String> = callbackFlow {
        val sessionId = repository.newTypingSessionUrl()

        if (sessionId == null) {
            Logger.error("sessionId is null")
            close()
            return@callbackFlow
        }

        // Generate and set the QR code when collection starts
        val appUrl = context.getString(R.string.online_typer_app_url)
        val bitmap = "$appUrl?id=$sessionId".getQrCode()
        _qrCodeBitmapState.value = bitmap

        // Listen for input updates
        repository.listenInput(sessionId) {
            trySend(it)
        }
        awaitClose {
            Logger.debug("Cleaning up session: $sessionId")
            _qrCodeBitmapState.value = null
            viewModelScope.launch {
                repository.deleteTypingSession()
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(100),
        initialValue = ""
    )
}
