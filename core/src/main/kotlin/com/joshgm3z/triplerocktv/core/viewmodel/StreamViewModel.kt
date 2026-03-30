package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.MovieMetadata
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
) : ViewModel() {

    private val categoryId = savedStateHandle.get<Int>("categoryId")
        ?: throw IllegalStateException("categoryId not found")

    private val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw IllegalStateException("streamType not found")

    private val _uiState = MutableStateFlow<List<Any>?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        fetchStreams(categoryId, streamType)
    }

    private fun fetchStreams(categoryId: Int, streamType: StreamType) {
        Logger.debug("categoryId = [${categoryId}], browseType = [${streamType}]")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = repository.fetchStreamsOfCategory(categoryId, streamType)
        }
    }

    suspend fun fetchMetadata(streamId: Int): MovieMetadata {
        return onlineRepository.getMovieMetadata(streamId)
    }
}