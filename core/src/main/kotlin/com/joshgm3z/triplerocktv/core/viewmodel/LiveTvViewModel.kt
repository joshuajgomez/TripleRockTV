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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

@OptIn(FlowPreview::class)
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

    var selectedStreamId = savedStateHandle.get<Int>("selectedStreamId")

    val streamListState: StateFlow<List<StreamData>?> = repository
        .fetchLiveStreamsOfCategoryFlow(categoryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _programUiState = MutableStateFlow<ProgramUiState?>(null)
    val programUiState = _programUiState.asStateFlow()

    private lateinit var userInfo: UserInfo

    val focusedStreamData: MutableStateFlow<StreamData?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            localDatastore.getUserInfo()?.let {
                userInfo = it
            }
        }
        viewModelScope.launch {
            focusedStreamData.debounce(100).collect {
                it?.let { streamData ->
                    onStreamDataFocused(streamData)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            onlineRepository.fetchStreams(StreamType.LiveTV, categoryId)
        }
    }

    private suspend fun onStreamDataFocused(streamData: StreamData) {
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

    fun updateMyList(
        streamData: StreamData,
        add: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(
                streamData.streamId,
                StreamType.LiveTV,
                add
            )
        }
    }
}