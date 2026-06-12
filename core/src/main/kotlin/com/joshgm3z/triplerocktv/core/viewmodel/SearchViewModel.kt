package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchHints: List<String> = emptyList(),
    val streams: List<Any> = emptyList(),
    val statusText: String = "",
    val showRecentAddedTitle: Boolean = false,
)

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: SearchRepository,
    private val mediaLocalRepository: MediaLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var recentStreams: List<Any> = emptyList()

    private var job: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentStreams = mediaLocalRepository.fetchNewlyAdded(StreamType.VideoOnDemand)
            _uiState.update {
                it.copy(
                    searchHints = repository.getSearchTextList(),
                    streams = recentStreams,
                    showRecentAddedTitle = recentStreams.isNotEmpty()
                )
            }
        }
    }

    fun onSearchInputChange(text: String) {
        this.job?.cancel()

        if (text.isEmpty()) {
            _uiState.update {
                it.copy(
                    statusText = "",
                    streams = recentStreams,
                    showRecentAddedTitle = recentStreams.isNotEmpty()
                )
            }
            return
        }
        _uiState.update { it.copy(statusText = "Searching...") }
        val job = viewModelScope.launch(Dispatchers.IO) {
            val searchResult = repository.searchStreamByName(
                text,
                StreamType.VideoOnDemand
            ) + repository.searchStreamByName(
                text,
                StreamType.LiveTV
            ) + repository.searchSeriesByName(text)

            _uiState.update {
                if (searchResult.isEmpty()) {
                    it.copy(
                        streams = recentStreams,
                        statusText = "No results found",
                        showRecentAddedTitle = true
                    )
                } else {
                    it.copy(
                        streams = searchResult,
                        statusText = "",
                        showRecentAddedTitle = false
                    )
                }
            }
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