package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MediaLocalRepository
) : ViewModel() {

    private val _stream = MutableStateFlow<VodStream?>(null)
    val stream = _stream.asStateFlow()

    fun fetchStreamDetails(streamId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.fetchStream(streamId)
            if (result is VodStream) {
                _stream.value = result
            }
        }
    }
}
