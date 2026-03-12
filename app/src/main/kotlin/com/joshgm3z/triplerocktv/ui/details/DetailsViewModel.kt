package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository
) : ViewModel() {

    private val _streamData = MutableStateFlow<StreamData?>(null)
    val streamData = _streamData.asStateFlow()

    var isBlurSettingEnabled: Boolean = false
    var serverUrl: String = ""

    private var streamId: Int? = null

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
            serverUrl = localDatastore.getUserInfo()?.webUrl ?: ""
        }
    }

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        this.streamId = streamId
        viewModelScope.launch {
            repository.streamDataFlow(streamId, streamType).collectLatest {
                _streamData.value = it
            }
        }
    }

    fun addToMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(streamId!!, true)
        }
    }

    fun removeFromMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(streamId!!, false)
        }
    }
}
