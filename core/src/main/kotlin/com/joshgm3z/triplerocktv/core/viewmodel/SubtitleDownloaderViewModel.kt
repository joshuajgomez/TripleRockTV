package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubtitleDownloaderViewModel
@Inject
constructor(
    private val subtitleRepository: SubtitleRepository
) : ViewModel() {

    private val _subtitleUiState = MutableStateFlow<List<SubtitleData>?>(null)
    val subtitleUiState = _subtitleUiState.asStateFlow()

    fun searchQuery(query: String) {
        viewModelScope.launch {
            _subtitleUiState.value = subtitleRepository.findSubtitles(query)
        }
    }

    suspend fun getSubtitleUrl(fileId: Int): String? {
        Logger.debug("fileId = [${fileId}]")
        return subtitleRepository.getSubtitleUrl(fileId)
    }

}