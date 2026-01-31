package com.joshgm3z.triplerocktv.ui.player

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaUiState(
    val streamEntity: StreamEntity?,
)

@HiltViewModel
class MediaInfoViewModel
@Inject constructor(
    private val repository: MediaLocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), IMediaInfoViewModel {
    private val _uiState = MutableStateFlow(MediaUiState(null))
    override val uiState = _uiState.asStateFlow()

    companion object {
        private const val TAG = "MediaInfoViewModel"
    }

    init {
        viewModelScope.launch {
            repository.fetchMediaDataById(
                StreamEntity.sample().streamId,
                onSuccess = { streamEntity ->
                    Log.i(TAG, "fetchMediaDataById.onSuccess $streamEntity")
                    _uiState.update {
                        it.copy(streamEntity = streamEntity)
                    }
                },
                onError = {},
            )
        }
    }
}