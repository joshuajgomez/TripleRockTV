package com.joshgm3z.triplerocktv.ui.browse.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
import com.joshgm3z.triplerocktv.util.Logger
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
    private val repository: MediaLocalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Any>>(emptyList())
    val uiState = _uiState.asStateFlow()

    fun fetchStreams(categoryId: Int, browseType: BrowseType) {
        Logger.debug("categoryId = [${categoryId}], browseType = [${browseType}]")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = repository.fetchStreamsOfCategory(categoryId, browseType)
        }
    }
}