package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgramUiState(
    val programs: List<Program> = emptyList(),
    val videoToPlay: String? = null,
)

data class Program(
    val title: String,
    val description: String? = null,
    val start: String,
    val stop: String,
    val isNowPlaying: Boolean = false,
)

@HiltViewModel
class LiveTvViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
    private val localDatastore: LocalDatastore
) : ViewModel() {

    private val categoryId = savedStateHandle.get<Int>("categoryId")
        ?: throw IllegalStateException("categoryId not found")

    private val _uiState = MutableStateFlow<List<StreamData>?>(null)
    val uiState = _uiState.asStateFlow()

    private val _programUiState = MutableStateFlow<ProgramUiState?>(null)
    val programUiState = _programUiState.asStateFlow()

    private lateinit var userInfo: UserInfo

    init {
        viewModelScope.launch {
            localDatastore.getUserInfo()?.let {
                userInfo = it
            }
        }
        fetchStreams(categoryId)
    }

    private fun fetchStreams(categoryId: Int) {
        Logger.debug("categoryId = [${categoryId}]")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = repository.fetchStreamsOfCategory(
                categoryId, StreamType.LiveTV
            ) as List<StreamData>
        }
    }

    fun onStreamDataFocused(streamData: StreamData) {
        viewModelScope.launch {
            val listings = onlineRepository.getShortEpgListing(streamData.streamId)
            _programUiState.value = ProgramUiState(
                videoToPlay = streamData.videoUrl(userInfo),
                programs = listings.map {
                    Program(
                        title = it.titleDecoded(),
                        description = it.descriptionDecoded(),
                        start = it.startTimestampFormatted(),
                        stop = it.stopTimestampFormatted(),
                        isNowPlaying = it.isNowPlaying()
                    )
                }
            )
        }
    }
}