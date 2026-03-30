package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiData(
    val searchHints: List<String> = emptyList(),
    val searchUiState: SearchUiState = SearchUiState.Initial,
)

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Result(
        val query: String,
        val streamDataList: List<StreamData>,
        val seriesStreams: List<SeriesStream>,
    ) : SearchUiState() {
        fun isEmpty() = streamDataList.isEmpty() && seriesStreams.isEmpty()
    }
}

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiData())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    searchHints = repository.getSearchTextList()
                )
            }
        }
    }

    fun onSearchInputChange(text: String) {
        this.job?.cancel()

        if (text.isEmpty()) {
            _uiState.update { it.copy(searchUiState = SearchUiState.Initial) }
            return
        }
        _uiState.update { it.copy(searchUiState = SearchUiState.Loading) }
        val job = viewModelScope.launch(Dispatchers.IO) {
            val searchUiState = SearchUiState.Result(
                query = text,
                streamDataList = repository.searchStreamByName(
                    text,
                    StreamType.VideoOnDemand
                ) + repository.searchStreamByName(
                    text,
                    StreamType.LiveTV
                ),
                seriesStreams = emptyList()
            )
            _uiState.update { it.copy(searchUiState = searchUiState) }
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