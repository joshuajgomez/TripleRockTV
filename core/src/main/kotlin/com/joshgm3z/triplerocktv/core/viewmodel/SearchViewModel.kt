package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    class Initial(
        val hints: List<String>,
        val initialStreams: List<StreamData>
    ) : SearchUiState()

    object Loading : SearchUiState()
    object NoResult : SearchUiState()
    class Result(
        val list: List<Any>
    ) : SearchUiState()
}

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: SearchRepository,
    private val mediaLocalRepository: MediaLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private var initialState: SearchUiState.Initial? = null

    private var job: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initialState = SearchUiState.Initial(
                hints = repository.getSearchTextList(),
                initialStreams = mediaLocalRepository.fetchNewlyAdded(StreamType.VideoOnDemand)
            )
            resetUiState()
        }
    }

    fun resetUiState() {
        viewModelScope.launch {
            _uiState.value = initialState
        }
    }

    fun onSearchInputChange(text: String) {
        this.job?.cancel()

        if (text.isEmpty()) {
            resetUiState()
            return
        }
        _uiState.value = SearchUiState.Loading

        val job = viewModelScope.launch(Dispatchers.IO) {
            val searchResult = repository.searchStreamByName(
                text,
                StreamType.VideoOnDemand
            ) + repository.searchStreamByName(
                text,
                StreamType.LiveTV
            ) + repository.searchSeriesByName(text)

            _uiState.value = if (searchResult.isEmpty()) SearchUiState.NoResult
            else SearchUiState.Result(list = searchResult)
        }
        this.job = job
        job.invokeOnCompletion {
            this.job = null
        }
    }

    fun saveSearchHint(hint: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSearchText(hint)
        }
    }
}