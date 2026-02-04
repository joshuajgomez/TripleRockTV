package com.joshgm3z.triplerocktv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Result(val query: String, val list: List<VodStream>) : SearchUiState()
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
        viewModelScope.launch {
            repository.searchStreamByName(text) {
                _uiState.value = SearchUiState.Result(text, it)
            }
        }
    }
}