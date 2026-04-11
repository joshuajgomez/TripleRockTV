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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _inputTextFlow = MutableStateFlow("")
    val inputTextFlow = _inputTextFlow.asStateFlow()
        get() {
            startInputTextFlow()
            return field
        }

    private fun startInputTextFlow() {
        Logger.debug("entry")
        viewModelScope.launch {
            val sessionId = repository.newTypingSessionId()
            Logger.debug("sessionId = [$sessionId]")
            if (sessionId == null) {
                Logger.error("sessionId is null")
                return@launch
            }

            val appUrl = context.getString(R.string.online_typer_app_url)
            val bitmap = "$appUrl?id=$sessionId".getQrCode()
            if (bitmap == null) {
                Logger.error("bitmap is null")
                return@launch
            }

            _qrCodeBitmapState.value = bitmap
            repository.listenInput(sessionId) {
                Logger.debug("input = $it")
                _inputTextFlow.value = it
            }
        }
    }
}