package com.joshgm3z.triplerocktv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.BrowseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Result(
        val query: String,
        val vodStreams: List<VodStream>,
        val liveTvStreams: List<LiveTvStream>,
        val seriesStreams: List<SeriesStream>,
    ) : SearchUiState() {
        fun isEmpty() = vodStreams.isEmpty() && liveTvStreams.isEmpty() && seriesStreams.isEmpty()
    }
}

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: MediaLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun onSearchInputChange(text: String) {
        if (text.isEmpty()) {
            _uiState.value = SearchUiState.Initial
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = SearchUiState.Result(
                query = text,
                vodStreams = repository.searchStreamByName(
                    text,
                    BrowseType.VideoOnDemand
                ) as List<VodStream>,
                seriesStreams = repository.searchStreamByName(
                    text,
                    BrowseType.Series
                ) as List<SeriesStream>,
                liveTvStreams = repository.searchStreamByName(
                    text,
                    BrowseType.LiveTV
                ) as List<LiveTvStream>,
            )
        }
    }
}