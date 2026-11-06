package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    class Initial(
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
    savedStateHandle: SavedStateHandle,
    private val repository: SearchRepository,
    private val mediaLocalRepository: MediaLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private var initialState: SearchUiState.Initial? = null

    private val _hintsState = MutableStateFlow<List<String>>(emptyList())
    val hintsState = _hintsState.asStateFlow()

    private var job: Job? = null

    val initialSearchText = savedStateHandle.get<String>("initialSearchText")
        .takeIf { !it.isNullOrEmpty() }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initialState = SearchUiState.Initial(
                initialStreams = mediaLocalRepository.fetchNewlyAdded(StreamType.VideoOnDemand)
                    .addStreamCategoryNames()
            )
            initialSearchText?.let {
                onSearchInputChange(it)
            } ?: resetUiState()
        }
        viewModelScope.launch {
            repository.searchHintsFlow().collectLatest {
                _hintsState.value = it
            }
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
            val searchResult = repository.searchStreamByName(text).addStreamCategoryNames() +
                    repository.searchSeriesByName(text).addSeriesCategoryNames()
            Logger.debug("searchResult = [$searchResult]")
            _uiState.value = if (searchResult.isEmpty()) SearchUiState.NoResult
            else SearchUiState.Result(list = searchResult)
        }
        this.job = job
        job.invokeOnCompletion {
            this.job = null
        }
    }

    private suspend fun List<StreamData>.addStreamCategoryNames(): List<StreamData> = map {
        it.apply {
            categoryName = mediaLocalRepository.getCategory(categoryId)?.categoryName
        }
    }

    private suspend fun List<SeriesStream>.addSeriesCategoryNames(): List<SeriesStream> = map {
        it.apply {
            categoryName = mediaLocalRepository.getCategory(categoryId)?.categoryName
        }
    }

    fun saveSearchHint(hint: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSearchText(hint)
        }
    }
}